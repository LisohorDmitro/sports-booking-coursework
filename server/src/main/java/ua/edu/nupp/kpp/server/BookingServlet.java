package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/bookings/*")
public class BookingServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(BookingServlet.class.getName());
    private final BookingService service = new BookingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = pathInfo(req);
            if (path == null || path.equals("/")) {
                JsonUtil.writeJson(resp, 200, service.getAll());
            } else {
                long id = Long.parseLong(path.substring(1));
                JsonUtil.writeJson(resp, 200, service.getById(id));
            }
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "GET bookings", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            JsonNode body = JsonUtil.MAPPER.readTree(req.getInputStream());
            String action = body.has("action") ? body.get("action").asText() : "create";

            if ("create".equals(action)) {
                long userId = body.get("userId").asLong();
                long timeSlotId = body.get("timeSlotId").asLong();
                double f1 = body.get("f1").asDouble();
                double f2 = body.get("f2").asDouble();
                double f3 = body.get("f3").asDouble();
                JsonUtil.writeJson(resp, 201, service.create(userId, timeSlotId, f1, f2, f3));
            } else if ("changeState".equals(action)) {
                long id = body.get("id").asLong();
                BookingState state = BookingState.valueOf(body.get("state").asText());
                JsonUtil.writeJson(resp, 200, service.changeState(id, state));
            } else if ("recalculate".equals(action)) {
                long id = body.get("id").asLong();
                double f1 = body.get("f1").asDouble();
                double f2 = body.get("f2").asDouble();
                double f3 = body.get("f3").asDouble();
                JsonUtil.writeJson(resp, 200, service.recalculate(id, f1, f2, f3));
            } else {
                JsonUtil.writeError(resp, 400, "Невідома action: " + action);
            }
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "POST bookings", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = pathInfo(req);
            if (path == null || path.equals("/")) {
                JsonUtil.writeError(resp, 400, "Потрібен id");
                return;
            }
            long id = Long.parseLong(path.substring(1));
            service.delete(id);
            resp.setStatus(204);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "DELETE bookings", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    private String pathInfo(HttpServletRequest req) {
        String pi = req.getPathInfo();
        return pi == null ? "/" : pi;
    }
}
