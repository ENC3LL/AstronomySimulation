import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

// Класс для хранения информации об объекте
class CelestialBody {
    String name;
    double mass;
    double distance;
    double majorSemiAxis;
    double eccentricity;
    double orbitalInclination;
    double radius;
    double gravityConst;
    double orbitalSpeed;
    double periodOfRevolution;
    double rotationPeriod;
    String classification;

    public CelestialBody(String name, double mass, double distance, double majorSemiAxis,
                         double eccentricity, double orbitalInclination, double radius,
                         double gravityConst, double orbitalSpeed, double periodOfRevolution,
                         double rotationPeriod, String classification) {
        this.name = name;
        this.mass = mass;
        this.distance = distance;
        this.majorSemiAxis = majorSemiAxis;
        this.eccentricity = eccentricity;
        this.orbitalInclination = orbitalInclination;
        this.radius = radius;
        this.gravityConst = gravityConst;
        this.orbitalSpeed = orbitalSpeed;
        this.periodOfRevolution = periodOfRevolution;
        this.rotationPeriod = rotationPeriod;
        this.classification = classification;
    }
}

public class QBody {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        input.useLocale(Locale.US);

        System.out.print("Количество объектов: ");
        int n = input.nextInt();

        // Минимальные массы для классификации
        double planetMin = 3.3e23;
        double starMin = 1.6e29;
        double BHMin = 6.0e30;

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

            System.out.print("Расстояние (метров): ");
            double distance = input.nextDouble();

            System.out.print("Большая полуось (метров): ");
            double majorSemiAxis = input.nextDouble();

            System.out.print("Эксцентриситет: ");
            double eccentricity = input.nextDouble();

            System.out.print("Орбитальное наклонение (градусов): ");
            double orbitalInclination = input.nextDouble();

            System.out.print("Радиус (метров): ");
            double radius = input.nextDouble();

            System.out.print("Гравитационная постоянная (м/с²): ");
            double gravityConst = input.nextDouble();

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
            CelestialBody body = new CelestialBody(name, mass, distance, majorSemiAxis,
                    eccentricity, orbitalInclination, radius, gravityConst,
                    orbitalSpeed, periodOfRevolution, rotationPeriod, classification);
            bodies.add(body);
        }

        // Вывод информации о всех объектах
        System.out.println("\n=== Информация о всех объектах ===");
        for (CelestialBody body : bodies) {
            System.out.println("\n--- " + body.name + " ---");
            System.out.println("Классификация: " + body.classification + " (" + body.mass + " кг)");
            System.out.println("Масса: " + body.mass + " кг");
            System.out.println("Расстояние: " + body.distance + " метров");
            System.out.println("Большая полуось: " + body.majorSemiAxis + " метров");
            System.out.println("Эксцентриситет: " + body.eccentricity);
            System.out.println("Орбитальное наклонение: " + body.orbitalInclination + " градусов");
            System.out.println("Радиус: " + body.radius + " метров");
            System.out.println("Гравитационная постоянная: " + body.gravityConst + " м/с²");
            System.out.println("Орбитальная скорость: " + body.orbitalSpeed + " м/с");
            System.out.println("Период обращения: " + body.periodOfRevolution + " секунд");
            System.out.println("Период вращения: " + body.rotationPeriod + " секунд");
        }

        input.close();
    }
}
