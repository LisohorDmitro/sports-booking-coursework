package ua.edu.nupp.kpp.server;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/facilities/*")
public class FacilityServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(FacilityServlet.class.getName());
    private final FacilityService service = new FacilityService();

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
            LOG.log(Level.SEVERE, "GET facilities", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Facility f = JsonUtil.MAPPER.readValue(req.getInputStream(), Facility.class);
            JsonUtil.writeJson(resp, 201, service.create(f));
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "POST facilities", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Facility f = JsonUtil.MAPPER.readValue(req.getInputStream(), Facility.class);
            JsonUtil.writeJson(resp, 200, service.update(f));
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "PUT facilities", e);
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
            LOG.log(Level.SEVERE, "DELETE facilities", e);
            JsonUtil.writeError(resp, 500, "Внутрішня помилка сервера");
        }
    }

    private String pathInfo(HttpServletRequest req) {
        String pi = req.getPathInfo();
        return pi == null ? "/" : pi;
    }
}
