import com.dlsc.gmapsfx.javascript.object.GoogleMap;
import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MVCArray;
import com.dlsc.gmapsfx.javascript.object.MapShape;
import com.dlsc.gmapsfx.shapes.Polyline;
import com.dlsc.gmapsfx.shapes.PolylineOptions;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Александр Холодов
 * @created 11/2020
 * @project GMapsFXDebug
 * @description Соединительная линия
 */
@Getter @Setter
public class MapLine {

    private String name;    // Название линии
    private MapShape shape; // Графика
    private LatLong start, end; // Начальная и конечная точка
    private GoogleMap map;  // Карта

    /** Что с чем соединить */
    public MapLine(MapElement from, MapElement to){
        start = from.getCenter();
        end = to.getCenter();

        MVCArray mvc = new MVCArray(new LatLong[]{ start, end });
        PolylineOptions polyOpts = new PolylineOptions().path(mvc).strokeColor("red").strokeWeight(2);
        this.shape = new Polyline(polyOpts);
    }

    /** Отрисовать на карте */
    public void addToMap(GoogleMap map){ this.map = map; map.addMapShape(shape);  }

    /** Удалить с карты */
    public void remove(){ if(map != null) { map.removeMapShape(shape); } }

}
