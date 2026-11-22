package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class HttpSportsClient {
    private static final String BASE = "http://localhost:8080/sports-booking-server";
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    // ---- Facility ----
    public List<Facility> getFacilities() throws Exception {
        return getList("/facilities", new TypeReference<>() {});
    }

    public Facility addFacility(Facility f) throws Exception {
        return post("/facilities", f, Facility.class);
    }

    public Facility updateFacility(Facility f) throws Exception {
        return put("/facilities", f, Facility.class);
    }

    public void deleteFacility(long id) throws Exception {
        delete("/facilities/" + id);
    }

    // ---- User ----
    public List<AppUser> getUsers() throws Exception {
        return getList("/users", new TypeReference<>() {});
    }

    public AppUser addUser(AppUser u) throws Exception {
        return post("/users", u, AppUser.class);
    }

    public AppUser updateUser(AppUser u) throws Exception {
        return put("/users", u, AppUser.class);
    }

    public void deleteUser(long id) throws Exception {
        delete("/users/" + id);
    }

    // ---- TimeSlot ----
    public List<TimeSlot> getTimeSlots() throws Exception {
        return getList("/timeslots", new TypeReference<>() {});
    }

    public TimeSlot addTimeSlot(TimeSlot t) throws Exception {
        return post("/timeslots", t, TimeSlot.class);
    }

    public TimeSlot updateTimeSlot(TimeSlot t) throws Exception {
        return put("/timeslots", t, TimeSlot.class);
    }

    public void deleteTimeSlot(long id) throws Exception {
        delete("/timeslots/" + id);
    }

    // ---- Booking ----
    public List<Booking> getBookings() throws Exception {
        return getList("/bookings", new TypeReference<>() {});
    }

    public Booking createBooking(long userId, long timeSlotId, double f1, double f2, double f3) throws Exception {
        Map<String, Object> body = Map.of(
                "action", "create",
                "userId", userId,
                "timeSlotId", timeSlotId,
                "f1", f1,
                "f2", f2,
                "f3", f3
        );
        return post("/bookings", body, Booking.class);
    }

    public Booking changeBookingState(long id, BookingState state) throws Exception {
        Map<String, Object> body = Map.of(
                "action", "changeState",
                "id", id,
                "state", state.name()
        );
        return post("/bookings", body, Booking.class);
    }

    public Booking recalculateBooking(long id, double f1, double f2, double f3) throws Exception {
        Map<String, Object> body = Map.of(
                "action", "recalculate",
                "id", id,
                "f1", f1,
                "f2", f2,
                "f3", f3
        );
        return post("/bookings", body, Booking.class);
    }

    public void deleteBooking(long id) throws Exception {
        delete("/bookings/" + id);
    }

    // ---- helpers ----
    private <T> List<T> getList(String path, TypeReference<List<T>> type) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .GET().timeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(resp);
        return mapper.readValue(resp.body(), type);
    }

    private <T> T post(String path, Object body, Class<T> type) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(resp);
        return mapper.readValue(resp.body(), type);
    }

    private <T> T put(String path, Object body, Class<T> type) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .timeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(resp);
        return mapper.readValue(resp.body(), type);
    }

    private void delete(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .DELETE().timeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
        }
    }

    private void check(HttpResponse<String> resp) {
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
        }
    }
}
