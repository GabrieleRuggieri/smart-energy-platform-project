package it.gabriele.energy.controller;

import io.quarkus.logging.Log;
import it.gabriele.energy.model.EnergyBrazilModel;
import it.gabriele.energy.service.EnergyBrazilService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/brazil")
@Produces(MediaType.APPLICATION_JSON)
public class EnergyBrazilController {

    @Inject
    EnergyBrazilService energyBrazilService;

    @GET
    @Path("/start")
    public Response startSimulation() {
        energyBrazilService.startHourlyDemandSimulation();
        return Response.ok("Simulazione sensori avviata").build();
    }

    @GET
    @Path("/records")
    public List<EnergyBrazilModel> getAllRecords() {

        Log.info("Inizio metodo getAllRecords()");

        return energyBrazilService.getAllFromMemory();
    }
}
