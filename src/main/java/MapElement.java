import com.dlsc.gmapsfx.javascript.object.*;
import com.dlsc.gmapsfx.shapes.Circle;
import com.dlsc.gmapsfx.shapes.CircleOptions;
import com.dlsc.gmapsfx.shapes.Rectangle;
import com.dlsc.gmapsfx.shapes.RectangleOptions;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Александр Холодов
 * @created 11/2020
 * @project GMapsFXDebug
 * @description Графический элемент
 */
@Getter @Setter
public class MapElement {

    private String name; // Название
    private MapShape shape; // Графика
    private InfoWindow infoWindow; // Надпись
    private LatLong latLong; // Положение

    private LatLong center; // Середина

    private GoogleMap map; // Карта на которой отрисован


    /**
     * Графический элемент на карте (Квадрат)
     * @param name - название
     * @param x - долгота
     * @param y - широта
     * @param width - длина в метрах
     * @param height - ширина в метрах
     */
    public MapElement(String name, double x, double y, double width, double height){
        this.name = name;
        this.latLong = new LatLong(x, y);

        LatLong startLL = new LatLong(latLong.getLatitude(), latLong.getLongitude());
        LatLong widthLL = startLL.getDestinationPoint(90, width), heightLL = startLL.getDestinationPoint(180, height);
        LatLong endLL = new LatLong(heightLL.getLatitude(), widthLL.getLongitude());
        LatLong infoPoint = new LatLong(latLong.getLatitude(), widthLL.getLongitude() + (latLong.getLongitude() - widthLL.getLongitude())/2);
        this.center = new LatLong(heightLL.getLatitude() + (latLong.getLatitude() - heightLL.getLatitude())/2, widthLL.getLongitude() + (latLong.getLongitude() - widthLL.getLongitude())/2);

        LatLongBounds llb = new LatLongBounds(startLL, endLL);
        RectangleOptions rOpts = new RectangleOptions()
                .bounds(llb)
                .strokeColor("WHITE")
                .strokeWeight(2)
                .fillColor("BLACK")
                .fillOpacity(0.8);

        this.shape = new Rectangle(rOpts);
//        shape.setEditable(true);

        InfoWindowOptions infoOptions = new InfoWindowOptions();
        infoOptions.content(String.format("<h12>%s</h12>", name)).position(infoPoint);
        infoWindow = new InfoWindow(infoOptions);
    }

    /**
     * Графический элемент на карте (Круг)
     * @param name - название
     * @param x - долгота
     * @param y - широта
     * @param radius - радиус в метрах
     */
    public MapElement(String name, double x, double y, double radius){
        this.name = name;
        this.latLong = new LatLong(x, y);

        LatLong centreC = new LatLong(latLong.getLatitude(), latLong.getLongitude());
        LatLong infoPoint = centreC.getDestinationPoint(0, radius);

        CircleOptions cOpts = new CircleOptions()
                .center(centreC)
                .radius(radius)
                .strokeColor("GREEN")
                .strokeWeight(2)
                .fillColor("RED")
                .fillOpacity(0.8);

        this.center = centreC;
        this.shape = new Circle(cOpts);
//        shape.setEditable(true);

        InfoWindowOptions infoOptions = new InfoWindowOptions();
        infoOptions.content(String.format("<h12>%s</h12>", name)).position(infoPoint);
        infoWindow = new InfoWindow(infoOptions);
    }


    /** Полигон (Если нужен) */
    @Deprecated
    private void initPoly(){
//        Polyline poly = new Polyline(polyOpts);
//        map.addMapShape(poly);
//
//        LatLong poly1 = new LatLong(47.429945, -122.84363);
//        LatLong poly2 = new LatLong(47.361153, -123.03040);
//        LatLong poly3 = new LatLong(47.387193, -123.11554);
//        LatLong poly4 = new LatLong(47.585789, -122.96722);
//        LatLong[] pAry = new LatLong[]{poly1, poly2, poly3, poly4};
//        MVCArray pmvc = new MVCArray(pAry);
//
//        PolygonOptions polygOpts = new PolygonOptions()
//                .paths(pmvc)
//                .strokeColor("blue")
//                .strokeWeight(2)
//                .editable(false)
//                .fillColor("lightBlue")
//                .fillOpacity(0.5);
//
//        Polygon pg = new Polygon(polygOpts);
//        map.addMapShape(pg);
//        map.addUIEventHandler(pg, UIEventType.click, (JSObject obj) -> {
//            //polygOpts.editable(true);
//            pg.setEditable(!pg.getEditable());
//        });
    }



    /** Отрисовать на карте */
    public void addToMap(GoogleMap map){ this.map = map; map.addMapShape(shape); infoWindow.open(map); }

    /** Удалить с карты */
    public void remove(){ if(map != null) { map.removeMapShape(shape); infoWindow.close(); } }

}
