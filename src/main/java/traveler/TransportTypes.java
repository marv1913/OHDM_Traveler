package traveler;

/**
 * @author : Enrico Gamil Toros de Chadarevian
 * Project name : HistoricRouteSearch
 * @version : 1.0
 * @since : 05.05.2020, Di.
 **/
public enum TransportTypes {
    //Default transport types
    Walking(6),
    Horse(50),
    Carriage(9),
    Car(60),
    Boat(12),
    Bicycle(15);

    private final int speed;

    TransportTypes(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public int getId(){return this.ordinal() + 1;}
}
