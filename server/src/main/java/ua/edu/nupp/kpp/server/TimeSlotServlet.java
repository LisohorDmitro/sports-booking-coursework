package ua.edu.nupp.kpp.server;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/timeslots/*")
public class TimeSlotServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(TimeSlotServlet.class.getName());
    private final TimeSlotService service = new TimeSlotService();

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
            LOG.log(Level.SEVERE, "GET timeslots", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            TimeSlot t = JsonUtil.MAPPER.readValue(req.getInputStream(), TimeSlot.class);
            JsonUtil.writeJson(resp, 201, service.create(t));
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "POST timeslots", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            TimeSlot t = JsonUtil.MAPPER.readValue(req.getInputStream(), TimeSlot.class);
            JsonUtil.writeJson(resp, 200, service.update(t));
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "PUT timeslots", e);
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
            LOG.log(Level.SEVERE, "DELETE timeslots", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    private String pathInfo(HttpServletRequest req) {
        String pi = req.getPathInfo();
        return pi == null ? "/" : pi;
    }
}
