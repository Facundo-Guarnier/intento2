package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.ListaOrdenesDTO;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class CatedraAPIService {

    private final Logger log = LoggerFactory.getLogger(CatedraAPIService.class);

    @Autowired
    OrdenService ordenService;

    ObjectMapper objectMapper = new ObjectMapper();

    //! Recibe una URL, hace una solicitud HTTP GET, y guarda TODAS las ordenes en la DB.
    public void get(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            JsonNode responseJsonNode = objectMapper.readTree(connection.getInputStream());

            ListaOrdenesDTO response = objectMapper.readValue(responseJsonNode.toString(), ListaOrdenesDTO.class);
            List<OrdenDTO> ordenesDTO = response.getOrdenes();

            //! Guarda las ordenes en la DB
            for (OrdenDTO ordenDTO : ordenesDTO) {
                ordenDTO.setEstado("PENDIENTE");
                ordenService.save(ordenDTO);
                System.out.println("\n ---------- Cargando ----------\nGuardando en la DB: " + ordenDTO.toString());
            }
        } catch (Exception e) {
            log.error("Error al hacer la solicitud HTTP", e);
        }
    }

    //TODO Arreglar el metodo para que funcione como el get de arriva
    public JsonNode getConJWT(String apiUrl) {
        // String jwtToken = System.getenv("JWT_TOKEN");
        String jwtToken =
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYWN1bmRvZ3Vhcm5pZXIiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzI5NzUzNzcyfQ.pklknWchQH_Y8kM8Is-XCfu6hYxWVJJqgg0rNBAH9IisOWKPW1n-jC3Xqecv6HFjwHvWc3nugiaB5gtMaNlShg";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode response = objectMapper.readTree(connection.getInputStream());
            return response;
        } catch (Exception e) {
            log.error("Error en la solicitud HTTP", e);
        }

        return null;
    }
}
//! https://www.mockachino.com/spaces/2e3476f6-949b-42
// Ejemplo, solo los 2 primeros tienen que fallar
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
