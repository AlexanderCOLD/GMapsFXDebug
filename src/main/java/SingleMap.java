import com.dlsc.gmapsfx.GoogleMapView;
import com.dlsc.gmapsfx.MapComponentInitializedListener;
import com.dlsc.gmapsfx.javascript.event.UIEventType;
import com.dlsc.gmapsfx.javascript.object.GoogleMap;
import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MapOptions;
import com.dlsc.gmapsfx.javascript.object.MapTypeIdEnum;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import netscape.javascript.JSObject;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author Александр Холодов
 * @created 11/2020
 * @project GMapsFXDebug
 * @description Окно с Google картой
 */
@Getter
public class SingleMap implements MapComponentInitializedListener {

    private final GoogleMapView mapComponent = new GoogleMapView(Locale.getDefault().getLanguage(), "");
    protected GoogleMap map;

    private final Button btnZoomIn = new Button("Zoom In");
    private final Button btnZoomOut = new Button("Zoom Out");
    private final Button btnType = new Button("Map type");

    private final Label lblZoom = new Label();
    private final Label lblCenter = new Label();
    private final Label lblClick = new Label();
    private final ComboBox<MapTypeIdEnum> mapTypeCombo = new ComboBox<>();

    private final BorderPane bp = new BorderPane();
    private final ToolBar tb = new ToolBar();

    private boolean initialized = false;


    public void initialize(final Stage stage) {
        mapComponent.addMapInitializedListener(this);

        btnZoomIn.setOnAction(e -> { map.zoomProperty().set(map.getZoom() + 1); });
        btnZoomOut.setOnAction(e -> { map.zoomProperty().set(map.getZoom() - 1); });
        mapTypeCombo.setOnAction( e -> map.setMapType(mapTypeCombo.getSelectionModel().getSelectedItem() ));
        btnType.setOnAction(e -> { map.setMapType(MapTypeIdEnum.HYBRID); });
        tb.getItems().addAll(btnZoomIn, btnZoomOut, mapTypeCombo, new Label("Zoom: "), lblZoom, new Label("Center: "), lblCenter, new Label("Click: "), lblClick);

        bp.setTop(tb);
        bp.setCenter(mapComponent);
        stage.setTitle("Debug map project");
        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void mapInitialized() {
        LatLong center = new LatLong(0, 0);

        MapOptions options = new MapOptions();
        options.center(center)
                .zoom(12)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .mapType(MapTypeIdEnum.HYBRID)
                .clickableIcons(false)
                .disableDefaultUI(true)
                .disableDoubleClickZoom(true)
                .keyboardShortcuts(false);

        map = mapComponent.createMap(options,false);

        map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            System.out.println("LatLong: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
            lblClick.setText(ll.toString());
        });

        mapTypeCombo.getItems().addAll( MapTypeIdEnum.ALL );

        lblCenter.setText(map.getCenter().toString());
        map.centerProperty().addListener((ObservableValue<? extends LatLong> obs, LatLong o, LatLong n) -> lblCenter.setText(n.toString()));

        lblZoom.setText(Integer.toString(map.getZoom()));
        map.zoomProperty().addListener((ObservableValue<? extends Number> obs, Number o, Number n) -> lblZoom.setText(n.toString()));

        initialized = true;
    }



    /** Список элементов */
    private final HashMap<String, MapElement> elements = new HashMap<>();

    /** Список линий */
    private final HashMap<String, MapLine> lines = new HashMap<>();


    /**
     * Добавить граф. элемент - квадрат (в FX потоке)
     * @param name - название
     * @param x - долгота
     * @param y - широта
     * @param width - длина в метрах
     * @param height - ширина в метрах
     */
    public void addElement(String name, double x, double y, double width, double height) {
        while (!initialized) try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
        Platform.runLater(() -> {
            MapElement element = new MapElement(name, x, y, width, height);
            element.addToMap(map);
            elements.put(element.getName() ,element);
            map.setCenter(element.getCenter()); // Переводим координаты на данный элемент
        });
    }

    /**
     * Добавить граф. элемент - круг (в FX потоке)
     * @param name - название
     * @param x - долгота
     * @param y - широта
     * @param radius - радиус в метрах
     */
    public void addElement(String name, double x, double y, double radius) {
        while (!initialized) try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
        Platform.runLater(() -> {
            MapElement element = new MapElement(name, x, y, radius);
            element.addToMap(map);
            elements.put(element.getName() ,element);
            map.setCenter(element.getCenter()); // Переводим координаты на данный элемент
        });
    }


    /**
     * Добавить соединение
     * @param name - Название (можно не указывать)
     * @param sourceName - навзание элемента источника
     * @param targetName - название элемента назначения
     */
    public void addLine(String name, String sourceName, String targetName){
        while (!initialized) try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
        Platform.runLater(() -> {
            MapElement source = elements.get(sourceName); if(source==null) { System.err.printf("Элемент %s отсутствует %n", sourceName); return; }
            MapElement target = elements.get(targetName); if(target==null) { System.err.printf("Элемент %s отсутствует %n", targetName); return; }

            MapLine line = new MapLine(source, target);
            line.setName(name != null ? name : String.format("%s <---> %s", sourceName, targetName));
            line.addToMap(map);
            lines.put(line.getName(), line);
        });
    }

    /** Удалить все элементы */
    public void clear(){
        while (!initialized) try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
        Platform.runLater(() -> {
            elements.values().forEach(MapElement::remove);
            lines.values().forEach(MapLine::remove);
        });
    }
}
