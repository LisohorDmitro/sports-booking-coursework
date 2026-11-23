package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public final class JsonUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);

    private JsonUtil() {}

    public static void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        MAPPER.writeValue(resp.getWriter(), body);
    }

    public static void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        writeJson(resp, status, Map.of("error", message));
    }
}
