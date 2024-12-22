package org.example;

public class CelestialBody {
    // Основные параметры
    private String name;
    private double mass; // кг
    private double radius; // м
    private double posX; // м
    private double posY; // м
    private double velX; // м/с
    private double velY; // м/с
    private double accX; // м/с²
    private double accY; // м/с²
    private String classification;

    // Дополнительные орбитальные параметры
    private double majorSemiAxis;      // Большая полуось (м)
    private double eccentricity;       // Эксцентриситет
    private double orbitalInclination; // Орбитальное наклонение (градусы)
    private double periodOfRevolution; // Период обращения (секунды)
    private double rotationPeriod;     // Период вращения (секунды)

    // Гравитационная постоянная
    private static final double G = 6.67430e-11; // м^3 кг^-1 с^-2

    // Конструктор
    public CelestialBody(String name, double mass, double radius, double posX, double posY,
                         double velX, double velY, String classification,
                         double majorSemiAxis, double eccentricity,
                         double orbitalInclination, double periodOfRevolution,
                         double rotationPeriod) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.posX = posX;
        this.posY = posY;
        this.velX = velX;
        this.velY = velY;
        this.accX = 0;
        this.accY = 0;
        this.classification = classification;
        this.majorSemiAxis = majorSemiAxis;
        this.eccentricity = eccentricity;
        this.orbitalInclination = orbitalInclination;
        this.periodOfRevolution = periodOfRevolution;
        this.rotationPeriod = rotationPeriod;
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public double getMass() { return mass; }
    public double getRadius() { return radius; }
    public double getPosX() { return posX; }
    public double getPosY() { return posY; }
    public double getVelX() { return velX; }
    public double getVelY() { return velY; }
    public double getAccX() { return accX; }
    public double getAccY() { return accY; }
    public String getClassification() { return classification; }
    public double getMajorSemiAxis() { return majorSemiAxis; }
    public double getEccentricity() { return eccentricity; }
    public double getOrbitalInclination() { return orbitalInclination; }
    public double getPeriodOfRevolution() { return periodOfRevolution; }
    public double getRotationPeriod() { return rotationPeriod; }

    public void setPosX(double posX) { this.posX = posX; }
    public void setPosY(double posY) { this.posY = posY; }
    public void setVelX(double velX) { this.velX = velX; }
    public void setVelY(double velY) { this.velY = velY; }
    public void setAccX(double accX) { this.accX = accX; }
    public void setAccY(double accY) { this.accY = accY; }
    public void setMajorSemiAxis(double majorSemiAxis) { this.majorSemiAxis = majorSemiAxis; }
    public void setEccentricity(double eccentricity) { this.eccentricity = eccentricity; }
    public void setOrbitalInclination(double orbitalInclination) { this.orbitalInclination = orbitalInclination; }
    public void setPeriodOfRevolution(double periodOfRevolution) { this.periodOfRevolution = periodOfRevolution; }
    public void setRotationPeriod(double rotationPeriod) { this.rotationPeriod = rotationPeriod; }

    // Расчёт расстояния до другого тела
    public double distanceTo(CelestialBody other) {
        double dx = other.posX - this.posX;
        double dy = other.posY - this.posY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Расчёт орбитальной энергии
    public double orbitalEnergy(CelestialBody other) {
        double kinetic = this.kineticEnergy();
        double potential = this.gravitationalPotentialEnergy(other);
        return kinetic + potential;
    }

    // Расчёт момента импульса относительно центра симуляции
    public double angularMomentum() {
        return this.mass * (this.posX * this.velY - this.posY * this.velX);
    }

    // Расчёт приливной силы, действующей на это тело от другого тела
    public double tidalForce(CelestialBody other) {
        double distance = this.distanceTo(other);
        return 2 * G * other.mass * this.radius / Math.pow(distance, 3);
    }

    // Расчёт приливного нагревания
    public double tidalHeating(CelestialBody other, double eccentricity, double mu, double Q) {
        double distance = this.distanceTo(other);
        return (21.0 / 2.0) * G * Math.pow(other.mass, 2) * Math.pow(this.radius, 5) * Math.pow(eccentricity, 2)
                / (Math.pow(distance, 6) * mu * Q);
    }

    // Расчёт сдвига перигелия (приближённая формула)
    public double relativisticPerihelionAdvance(CelestialBody other) {
        double c = 3.0e8; // скорость света м/с
        double a = this.majorSemiAxis; // большая полуось орбиты
        double e = this.eccentricity; // эксцентриситет
        return (6 * Math.PI * G * other.mass) / (a * (1 - e * e) * Math.pow(c, 2));
    }

    // Расчёт гравитационной силы, действующей на это тело от другого тела
    public double[] gravitationalForce(CelestialBody other) {
        double dx = other.posX - this.posX;
        double dy = other.posY - this.posY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return new double[]{0, 0}; // Избегаем деления на ноль
        double force = G * this.mass * other.mass / (distance * distance);
        double forceX = force * dx / distance;
        double forceY = force * dy / distance;
        return new double[]{forceX, forceY};
    }

    // Метод для обновления ускорения на основе силы
    public synchronized void updateAcceleration(double forceX, double forceY) {
        this.accX += forceX / this.mass;
        this.accY += forceY / this.mass;
    }

    // Сброс ускорения перед новым шагом
    public synchronized void resetAcceleration() {
        this.accX = 0;
        this.accY = 0;
    }

    // Метод для обновления позиции и скорости (метод Рунге-Кутты или Эйлера)
    public synchronized void updatePositionAndVelocity(double dt) {
        // Обновление скорости
        this.velX += this.accX * dt;
        this.velY += this.accY * dt;

        // Обновление позиции
        this.posX += this.velX * dt;
        this.posY += this.velY * dt;
    }

    // Расчёт гравитационной потенциальной энергии с другим телом
    public double gravitationalPotentialEnergy(CelestialBody other) {
        double distance = this.distanceTo(other);
        if (distance == 0) return 0; // Избегаем деления на ноль
        return -G * this.mass * other.mass / distance;
    }

    // Расчёт кинетической энергии
    public double kineticEnergy() {
        return 0.5 * this.mass * (this.velX * this.velX + this.velY * this.velY);
    }

    // Метод для отображения состояния тела с нумерацией
    public void printState(int index) {
        double kinetic = kineticEnergy();
        double angularMomentum = angularMomentum();
        System.out.printf("%d: %s: Position=(%.2e, %.2e) m, Velocity=(%.2e, %.2e) m/s, Acceleration=(%.2e, %.2e) m/s², Kinetic Energy=%.2e J, Angular Momentum=%.2e kg·m²/s%n",
                index, name, posX, posY, velX, velY, accX, accY, kinetic, angularMomentum);
    }
}
