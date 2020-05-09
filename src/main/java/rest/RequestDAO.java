package rest;

import java.util.List;

public class RequestDAO implements SearchRequestDAO {

    private String startPointLongitude;
    private String startPointLatitude;
    private String endPointLongitude;
    private String endPointLatitude;

    private String day;
    private String peopleType;


    private String transportType;
    private List<String[]> restrictedArea;
    private List<String> closedFor;
    private String requestId;
    private String waterwayIncluded;

    public RequestDAO(String startPointLongitude, String startPointLatitude, String endPointLongitude, String endPointLatitude, String day, String peopleType, String transportType, String waterwayIncluded, List<String[]> restrictedArea, List<String> closedFor, String requestId) {
        this.startPointLongitude=startPointLongitude;
        this.startPointLatitude=startPointLatitude;
        this.endPointLongitude=endPointLongitude;
        this.endPointLatitude=endPointLatitude;
        this.day=day;
        this.peopleType=peopleType;
        this.closedFor=closedFor;
        this.restrictedArea=restrictedArea;
        this.transportType=transportType;
        this.requestId=requestId;
        this.waterwayIncluded = waterwayIncluded;
    }

    public String getStartPointLongitude() {
        return startPointLongitude;
    }

    @Override
    public void setStartPointLongitude(String startPointLongitude) {
        this.startPointLongitude = startPointLongitude;
    }

    @Override
    public void setEndPointLatitude(String latitude) {
        this.endPointLatitude=latitude;
    }

    @Override
    public String getStartPointLatitude() {
        return startPointLatitude;
    }

    @Override
    public void setStartPointLatitude(String startPointLatitude) {
        this.startPointLatitude = startPointLatitude;
    }

    public String getEndPointLongitude() {
        return endPointLongitude;
    }

    @Override
    public void setEndPointLongitude(String endPointLongitude) {
        this.endPointLongitude = endPointLongitude;
    }

    public String getEndPointLatitude() {
        return endPointLatitude;
    }


    public String getDay() {
        return day;
    }

    @Override
    public void setDay(String day) {
        this.day = day;
    }

    public String getPeopleType() {
        return peopleType;
    }

    @Override
    public void setPeopleType(String peopleType) {
        this.peopleType = peopleType;
    }

    public String getTransportType() {
        return transportType;
    }

    @Override
    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public List<String[]> getRestrictedArea() {
        return restrictedArea;
    }

    @Override
    public String getWaterwayIncluded() {
        return waterwayIncluded;
    }

    @Override
    public List<String> getClosedFor() {
        return this.closedFor;
    }

    @Override
    public void setRestrictedArea(List<String[]> restrictedArea) {
        this.restrictedArea = restrictedArea;
    }

    @Override
    public void setWaterwayIncluded(String waterwayIncluded) {
        this.waterwayIncluded=waterwayIncluded;
    }



    public void setClosedFor(List<String> closedFor) {
        this.closedFor = closedFor;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }







}
