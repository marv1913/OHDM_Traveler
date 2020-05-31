package traveler;

/**
 * @author : Enrico Gamil Toros de Chadarevian
 * Project name : HistoricRouteSearch
 * @version : 1.0
 * @since : 05.05.2020, Di.
 **/
public enum PeopleTypes {
    //Default types of People traveling
    Farmer(),
    Noble();

    public int getId(){return this.ordinal() + 1;}

    public static PeopleTypes getPeopleTypeFromSting(String str) {
        for (PeopleTypes me : PeopleTypes.values()) {
            if (me.name().equalsIgnoreCase(str))
                return me;
        }
        return null;
    }
}
