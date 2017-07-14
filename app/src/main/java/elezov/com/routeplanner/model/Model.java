package elezov.com.routeplanner.model;



import java.util.List;

import elezov.com.routeplanner.model.data.Direction.Route;
import rx.Observable;

public interface Model {

    Observable<List<Route>> getRouteList(String origin,String destination,String waypoints);
}
