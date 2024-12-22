package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Simulation {
    private List<CelestialBody> bodies;
    private double timeStep; // секунд
    private boolean running;
    private ExecutorService executor;
    private double accelerationFactor; // фактор ускорения симуляции

    public Simulation(List<CelestialBody> bodies, double timeStep, double accelerationFactor) {
        this.bodies = bodies;
        this.timeStep = timeStep;
        this.accelerationFactor = accelerationFactor;
        this.running = false;
        // Создаём пул потоков с числом потоков равным количеству доступных процессоров
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    // Метод для запуска симуляции в отдельном потоке
    public void startSimulation() {
        running = true;
        // Используем ScheduledExecutorService для периодического выполнения шагов симуляции
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // Рассчитываем период выполнения задач с учётом ускорения
        long period = (long) (timeStep / accelerationFactor * 1000); // миллисекунды

        scheduler.scheduleAtFixedRate(() -> {
            if (running) {
                stepRK4();
                printStates();
            }
        }, 0, period, TimeUnit.MILLISECONDS);

        // Добавляем обработчик завершения симуляции
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            scheduler.shutdown();
            executor.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                executor.shutdownNow();
            }
            System.out.println("\nСимуляция завершена.");
        }));
    }

    // Один шаг симуляции с использованием метода Рунге-Кутты 4-го порядка
    private void stepRK4() {
        int n = bodies.size();
        // Создание копий текущих состояний
        double[] k1_vx = new double[n];
        double[] k1_vy = new double[n];
        double[] k1_ax = new double[n];
        double[] k1_ay = new double[n];
        double[] k2_vx = new double[n];
        double[] k2_vy = new double[n];
        double[] k2_ax = new double[n];
        double[] k2_ay = new double[n];
        double[] k3_vx = new double[n];
        double[] k3_vy = new double[n];
        double[] k3_ax = new double[n];
        double[] k3_ay = new double[n];
        double[] k4_vx = new double[n];
        double[] k4_vy = new double[n];
        double[] k4_ax = new double[n];
        double[] k4_ay = new double[n];

        // Шаг 1: k1
        computeAccelerations();
        for (int i = 0; i < n; i++) {
            k1_vx[i] = bodies.get(i).getAccX() * timeStep;
            k1_vy[i] = bodies.get(i).getAccY() * timeStep;
            k1_ax[i] = bodies.get(i).getAccX();
            k1_ay[i] = bodies.get(i).getAccY();
        }

        // Шаг 2: k2
        // Временное обновление позиций и скоростей
        List<CelestialBody> tempBodies = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            CelestialBody original = bodies.get(i);
            CelestialBody temp = new CelestialBody(
                    original.getName(),
                    original.getMass(),
                    original.getRadius(),
                    original.getPosX() + 0.5 * k1_vx[i],
                    original.getPosY() + 0.5 * k1_vy[i],
                    original.getVelX() + 0.5 * k1_ax[i] * timeStep,
                    original.getVelY() + 0.5 * k1_ay[i] * timeStep,
                    original.getClassification(),
                    original.getMajorSemiAxis(),
                    original.getEccentricity(),
                    original.getOrbitalInclination(),
                    original.getPeriodOfRevolution(),
                    original.getRotationPeriod()
            );
            tempBodies.add(temp);
        }
        computeAccelerationsRK4(tempBodies);
        for (int i = 0; i < n; i++) {
            k2_vx[i] = tempBodies.get(i).getAccX() * timeStep;
            k2_vy[i] = tempBodies.get(i).getAccY() * timeStep;
            k2_ax[i] = tempBodies.get(i).getAccX();
            k2_ay[i] = tempBodies.get(i).getAccY();
        }

        // Шаг 3: k3
        tempBodies = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            CelestialBody original = bodies.get(i);
            CelestialBody temp = new CelestialBody(
                    original.getName(),
                    original.getMass(),
                    original.getRadius(),
                    original.getPosX() + 0.5 * k2_vx[i],
                    original.getPosY() + 0.5 * k2_vy[i],
                    original.getVelX() + 0.5 * k2_ax[i] * timeStep,
                    original.getVelY() + 0.5 * k2_ay[i] * timeStep,
                    original.getClassification(),
                    original.getMajorSemiAxis(),
                    original.getEccentricity(),
                    original.getOrbitalInclination(),
                    original.getPeriodOfRevolution(),
                    original.getRotationPeriod()
            );
            tempBodies.add(temp);
        }
        computeAccelerationsRK4(tempBodies);
        for (int i = 0; i < n; i++) {
            k3_vx[i] = tempBodies.get(i).getAccX() * timeStep;
            k3_vy[i] = tempBodies.get(i).getAccY() * timeStep;
            k3_ax[i] = tempBodies.get(i).getAccX();
            k3_ay[i] = tempBodies.get(i).getAccY();
        }

        // Шаг 4: k4
        tempBodies = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            CelestialBody original = bodies.get(i);
            CelestialBody temp = new CelestialBody(
                    original.getName(),
                    original.getMass(),
                    original.getRadius(),
                    original.getPosX() + k3_vx[i],
                    original.getPosY() + k3_vy[i],
                    original.getVelX() + k3_ax[i] * timeStep,
                    original.getVelY() + k3_ay[i] * timeStep,
                    original.getClassification(),
                    original.getMajorSemiAxis(),
                    original.getEccentricity(),
                    original.getOrbitalInclination(),
                    original.getPeriodOfRevolution(),
                    original.getRotationPeriod()
            );
            tempBodies.add(temp);
        }
        computeAccelerationsRK4(tempBodies);
        for (int i = 0; i < n; i++) {
            k4_vx[i] = tempBodies.get(i).getAccX() * timeStep;
            k4_vy[i] = tempBodies.get(i).getAccY() * timeStep;
            k4_ax[i] = tempBodies.get(i).getAccX();
            k4_ay[i] = tempBodies.get(i).getAccY();
        }

        // Обновление позиций и скоростей с использованием взвешенных коэффициентов
        for (int i = 0; i < n; i++) {
            CelestialBody body = bodies.get(i);
            double newVelX = body.getVelX() + (k1_ax[i] + 2 * k2_ax[i] + 2 * k3_ax[i] + k4_ax[i]) / 6.0 * timeStep;
            double newVelY = body.getVelY() + (k1_ay[i] + 2 * k2_ay[i] + 2 * k3_ay[i] + k4_ay[i]) / 6.0 * timeStep;
            double newPosX = body.getPosX() + (k1_vx[i] + 2 * k2_vx[i] + 2 * k3_vx[i] + k4_vx[i]) / 6.0;
            double newPosY = body.getPosY() + (k1_vy[i] + 2 * k2_vy[i] + 2 * k3_vy[i] + k4_vy[i]) / 6.0;

            body.setVelX(newVelX);
            body.setVelY(newVelY);
            body.setPosX(newPosX);
            body.setPosY(newPosY);
        }

        // После обновления позиций, проверяем столкновения
        detectCollisions();
    }

    // Метод для расчёта ускорений для текущего состояния тел
    private void computeAccelerations() {
        // Сброс ускорений
        for (CelestialBody body : bodies) {
            body.resetAcceleration();
        }

        // Расчёт всех гравитационных взаимодействий с использованием многопоточности
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < bodies.size(); i++) {
            CelestialBody bodyA = bodies.get(i);
            int finalI = i;
            Future<?> future = executor.submit(() -> {
                for (int j = 0; j < bodies.size(); j++) {
                    if (finalI == j) continue;
                    CelestialBody bodyB = bodies.get(j);
                    double[] force = bodyA.gravitationalForce(bodyB);
                    bodyA.updateAcceleration(force[0], force[1]);
                }
            });
            futures.add(future);
        }

        // Ожидание завершения всех задач
        for (Future<?> future : futures) {
            try {
                future.get(); // ожидание завершения
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для расчёта ускорений для временных тел (используется в Рунге-Кутты)
    private void computeAccelerationsRK4(List<CelestialBody> tempBodies) {
        int n = tempBodies.size();
        // Сброс ускорений
        for (CelestialBody body : tempBodies) {
            body.resetAcceleration();
        }

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            CelestialBody bodyA = tempBodies.get(i);
            int finalI = i;
            Future<?> future = executor.submit(() -> {
                for (int j = 0; j < n; j++) {
                    if (finalI == j) continue;
                    CelestialBody bodyB = tempBodies.get(j);
                    double[] force = bodyA.gravitationalForce(bodyB);
                    bodyA.updateAcceleration(force[0], force[1]);
                }
            });
            futures.add(future);
        }

        // Ожидание завершения всех задач
        for (Future<?> future : futures) {
            try {
                future.get(); // ожидание завершения
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для обнаружения столкновений
    private void detectCollisions() {
        List<String> collisionReports = new ArrayList<>();
        int n = bodies.size();
        for (int i = 0; i < n; i++) {
            CelestialBody bodyA = bodies.get(i);
            for (int j = i + 1; j < n; j++) {
                CelestialBody bodyB = bodies.get(j);
                double distance = bodyA.distanceTo(bodyB);
                if (distance <= (bodyA.getRadius() + bodyB.getRadius())) {
                    String report = String.format("Столкновение: %s (%.2e кг) и %s (%.2e кг) на позиции (%.2e, %.2e) м",
                            bodyA.getName(), bodyA.getMass(),
                            bodyB.getName(), bodyB.getMass(),
                            (bodyA.getPosX() + bodyB.getPosX()) / 2,
                            (bodyA.getPosY() + bodyB.getPosY()) / 2);
                    collisionReports.add(report);
                }
            }
        }

        // Если есть столкновения, выводим их
        if (!collisionReports.isEmpty()) {
            System.out.println("\n=== Столкновения ===");
            for (String report : collisionReports) {
                System.out.println(report);
            }
        }
    }

    // Метод для вывода состояния всех тел с использованием ANSI escape codes
    private void printStates() {
        // Очистка экрана и возврат курсора в начало
        System.out.print("\u001b[H\u001b[2J");
        System.out.flush();

        System.out.println("=== Текущее состояние объектов ===");
        int index = 1;
        for (CelestialBody body : bodies) {
            body.printState(index++);
        }
    }

    // Метод для остановки симуляции (можно вызвать при необходимости)
    public void stopSimulation() {
        running = false;
        executor.shutdown();
    }
}
