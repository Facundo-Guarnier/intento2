package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class OperadorDeOrdenesService {

    private final Logger log = LoggerFactory.getLogger(OperadorDeOrdenesService.class);

    @Autowired
    OrdenService ordenService;

    @Autowired
    ReportarOperacionesService ros;

    @Autowired
    CatedraAPIService catedraAPIService;

    //! Para cuando no puede realizarse la operación.
    public OrdenDTO noEsPosibleOperar(OrdenDTO orden) {
        log.debug("No es posible realizar la operacion");
        ordenService.update(orden);
        return orden;
    }

    //! Para cuando puede realizarse la operación.
    public OrdenDTO esPosibleOperar(OrdenDTO orden) {
        log.debug("Es posible realizar la operacion " + orden.getId());

        if (orden.getPrecio() == null) {
            orden = cambiarPrecio(orden);
        }

        if (!orden.getModo().equals("AHORA")) {
            programarOrden(orden);
        } else if (orden.getOperacion().equals("COMPRA")) {
            comprarOrden(orden);
        } else if (orden.getOperacion().equals("VENTA")) {
            venderOrden(orden);
        }

        return orden;
    }

    //! Programar la orden.
    public void programarOrden(OrdenDTO orden) {
        log.debug("Programando operacion");
        orden.setEstado(2);
        ordenService.update(orden);
    }

    //! Comprar la orden.
    public boolean venderOrden(OrdenDTO orden) {
        log.debug("Vendiendo orden");
        orden.setEstado(3);
        ordenService.update(orden);
        return true;
    }

    //! Vender la orden.
    public boolean comprarOrden(OrdenDTO orden) {
        log.debug("Comprando orden");
        orden.setEstado(3);
        ordenService.update(orden);
        return true;
    }

    //! Cambia el precio de la orden por el precio actual de la acción.
    public OrdenDTO cambiarPrecio(OrdenDTO orden) {
        String URL = "http://192.168.194.254:8000/api/acciones/ultimovalor/" + orden.getAccion();
        JsonNode precioJson = catedraAPIService.getConJWT(URL);
        Double precio = precioJson.get("ultimoValor").get("valor").asDouble();
        orden.setPrecio(precio.floatValue());
        return orden;
    }
}
