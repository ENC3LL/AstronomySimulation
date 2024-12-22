package org.example;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

// Основной класс для запуска симуляции
public class QBody {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        input.useLocale(Locale.US);

        System.out.print("Количество объектов: ");
        int n = input.nextInt();

        // Минимальные массы для классификации
        double planetMin = 3.3e23;   // Масса Меркурия
        double starMin = 1.6e29;     // Примерно 0.08 солнечных масс
        double BHMin = 6.0e30;       // Примерно 3 солнечные массы

        // Список для хранения объектов
        ArrayList<CelestialBody> bodies = new ArrayList<>();

        // Сбор информации о каждом объекте
        for (int i = 1; i <= n; i++) {
            System.out.println("\n--- Ввод информации для объекта " + i + " ---");

            System.out.print("Название объекта: ");
            input.nextLine(); // Очистка буфера
            String name = input.nextLine();

            System.out.print("Масса (кг): ");
            double mass = input.nextDouble();

            System.out.print("Расстояние от центра симуляции (метров): ");
            double distance = input.nextDouble();

            System.out.print("Большая полуось (метров): ");
            double majorSemiAxis = input.nextDouble();

            System.out.print("Эксцентриситет: ");
            double eccentricity = input.nextDouble();

            System.out.print("Орбитальное наклонение (градусов): ");
            double orbitalInclination = input.nextDouble();

            System.out.print("Радиус (метров): ");
            double radius = input.nextDouble();

            // Удалён ввод gravityConst

            System.out.print("Орбитальная скорость (м/с): ");
            double orbitalSpeed = input.nextDouble();

            System.out.print("Период обращения (секунд): ");
            double periodOfRevolution = input.nextDouble();

            System.out.print("Период вращения (секунд): ");
            double rotationPeriod = input.nextDouble();

            // Классификация тела по массе
            String classification;
            if (mass >= BHMin) {
                classification = "Черная дыра";
            } else if (mass >= starMin) {
                classification = "Звезда";
            } else if (mass >= planetMin) {
                classification = "Планета";
            } else {
                classification = "Не классифицировано";
            }

            // Создание и добавление объекта в список
            // Передача дополнительных параметров в конструктор
            CelestialBody body = new CelestialBody(
                    name,
                    mass,
                    radius,
                    distance, // posX
                    0,        // posY
                    0,        // velX
                    orbitalSpeed, // velY
                    classification,
                    majorSemiAxis,
                    eccentricity,
                    orbitalInclination,
                    periodOfRevolution,
                    rotationPeriod
            );
            bodies.add(body);
        }

        // Запрос на ускорение симуляции
        System.out.print("\nВведите фактор ускорения симуляции (например, 1 для нормальной скорости): ");
        double accelerationFactor = input.nextDouble();

        // Вывод информации о всех объектах
        System.out.println("\n=== Информация о всех объектах ===");
        int index = 1;
        for (CelestialBody body : bodies) {
            System.out.println("\n--- " + index + ": " + body.getName() + " ---");
            System.out.println("Классификация: " + body.getClassification() + " (" + body.getMass() + " кг)");
            System.out.println("Масса: " + body.getMass() + " кг");
            System.out.println("Позиция: (" + body.getPosX() + ", " + body.getPosY() + ") метров");
            System.out.println("Скорость: (" + body.getVelX() + ", " + body.getVelY() + ") м/с");
            System.out.println("Радиус: " + body.getRadius() + " метров");
            System.out.println("Большая полуось: " + body.getMajorSemiAxis() + " метров");
            System.out.println("Эксцентриситет: " + body.getEccentricity());
            System.out.println("Орбитальное наклонение: " + body.getOrbitalInclination() + " градусов");
            System.out.println("Период обращения: " + body.getPeriodOfRevolution() + " секунд");
            System.out.println("Период вращения: " + body.getRotationPeriod() + " секунд");
            index++;
        }

        input.close();

        if (bodies.isEmpty()){
            System.out.println("Нет объектов для расчётов");
            return;
        }

        // Запуск симуляции
        Simulation simulation = new Simulation(bodies, 1.0, accelerationFactor); // шаг 1 секунда
        simulation.startSimulation();
    }
}
