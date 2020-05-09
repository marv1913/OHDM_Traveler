package rest;

import java.util.List;

public interface SearchRequestDAO {

    public void setRequestId(String id);

    public void setStartPointLatitude(String latitude);

    public void setStartPointLongitude(String longitude);

    public void setEndPointLatitude(String latitude);

    public void setEndPointLongitude(String longitude);

    public void setDay(String day);

    public void setPeopleType(String peopleType);

    public void setTransportType(String transportType);

    public void setRestrictedArea(List<String[]> coordinates); // at least three coordinates tuple required

    public void setWaterwayIncluded(String waterwayIncluded);

    public void setClosedFor(List<String> closedFor);

    public String getRequestId();

    public String getStartPointLatitude();

    public String getStartPointLongitude();

    public String getEndPointLatitude();

    public String getEndPointLongitude();

    public String getDay();

    public String getPeopleType();

    public String getTransportType();

    public List<String[]> getRestrictedArea(); // at least three coordinates required

    public String getWaterwayIncluded();

    public List<String> getClosedFor();

}
