import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Александр Холодов
 * @created 11/2020
 * @project GMapsFXDebug
 * @description
 */
public class Main extends Application {

    /** Один экземпляр карт */
    private static final SingleMap singleMap = new SingleMap();

    @Override
    public void start(Stage primaryStage) { singleMap.initialize(primaryStage); }


    /**
     * ПС: 50.6861, 36.8689
     * Система 1: 50.8320, 37.0037
     * Система 2: 50.7015, 37.2079
     * Узлы 16,17,18: 50.7000, 36.7623
     * Узел 19: 50.6919, 36.7762
     *
     * Новые потребители: 50.6884, 36.7990 50.6888, 36.8031 50.6911, 36.8017
     * 50.6981, 36.7507 50.7072, 36.7821 50.7071, 36.7851 50.7037, 36.8394
     * @return
     */


    public static void main(String[] args) {

        SingleMap.keyAPI = args[0].trim();

        new Thread(() -> {

            /* Добавление элементов */
            singleMap.addElement("ПС", 50.6861, 36.8689, 500, 500);
            singleMap.addElement("Система 1", 50.8320, 37.0037, 200, 200);
            singleMap.addElement("Система 2", 50.7015, 37.2079, 100, 100);
            singleMap.addElement("Узел 1", 50.6919, 36.7762, 700);

            /* Добавление соединений */
            singleMap.addLine("Соединение 1", "ПС", "Система 1");
            singleMap.addLine("Соединение 2", "ПС", "Система 2");
            singleMap.addLine("Соединение 3", "ПС", "Узел 1");

//            try { Thread.sleep(3000); } catch (InterruptedException ignored) { } finally {
//                singleMap.clear();
//            }
        }).start();

        launch(args);
    }

}
