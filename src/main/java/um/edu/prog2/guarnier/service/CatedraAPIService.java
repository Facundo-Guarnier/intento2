package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CatedraAPIService {

    private final Logger log = LoggerFactory.getLogger(CatedraAPIService.class);
    private static final String REPORTE_URL = "http://192.168.194.254:8000/api/reporte-operaciones/reportar";
    private static final String JWT_TOKEN =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYWN1bmRvZ3Vhcm5pZXIiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzI5NzUzNzcyfQ.pklknWchQH_Y8kM8Is-XCfu6hYxWVJJqgg0rNBAH9IisOWKPW1n-jC3Xqecv6HFjwHvWc3nugiaB5gtMaNlShg";

    @Autowired
    OrdenService ordenService;

    ObjectMapper objectMapper = new ObjectMapper();

    //! Recibe una URL, hace una solicitud HTTP GET, y guarda TODAS las ordenes en la DB.
    public JsonNode get(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            JsonNode responseJsonNode = objectMapper.readTree(connection.getInputStream());

            return responseJsonNode;
        } catch (Exception e) {
            log.error("Error al hacer la get HTTP sin JWT.", e);
            return null;
        }
    }

    //! Recibe una URL, hace una solicitud HTTP GET, y guarda TODAS las ordenes en la DB.
    public JsonNode getConJWT(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JWT_TOKEN);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJsonNode = objectMapper.readTree(connection.getInputStream());

            return responseJsonNode;
        } catch (Exception e) {
            log.error("Error en la get HTTP con JWT.", e);
            return null;
        }
    }

    public void postRoprtar(JsonNode ordenes) {
        try {
            URL url = new URL(REPORTE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + JWT_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String ordenesJSON = new ObjectMapper().writeValueAsString(ordenes);

            //! Envía la solicitud
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = ordenesJSON.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            //! Recibe la respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.debug("Reporte exitoso.");
            } else {
                log.error("Error en la solicitud HTTP. Código de respuesta: " + responseCode);
            }
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el informe de operaciones a JSON.", e);
        } catch (Exception e) {
            log.error("Error en la solicitud HTTP con JWT.", e);
        }
    }
}
//! Ejemplo de JSON que recibe, solo los 2 primeros tienen que fallar
//! https://www.mockachino.com/spaces/2e3476f6-949b-42
// {
//   "ordenes": [
//     {
//       "cliente": 26364,
//       "accionId": 1,
//       "accion": "AAPL",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 10,
//       "fechaOperacion": "2023-09-25T03:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 3,
//       "accion": "INTC",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 0,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 2,
//       "accion": "GOOGL",
//       "operacion": "VENTA",
//       "precio": null,
//       "cantidad": 5,
//       "fechaOperacion": "2023-09-25T03:00:00Z",
//       "modo": "FINDIA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 4,
//       "accion": "KO",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 80,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 6,
//       "accion": "YPF",
//       "operacion": "VENTA",
//       "precio": null,
//       "cantidad": 5,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     }
//   ]
// }
