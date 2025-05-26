package it.gabriele.energy.controller;

import io.quarkus.logging.Log;
import it.gabriele.energy.model.EnergyRecord;
import it.gabriele.energy.service.EnergyService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/energy")
@Produces(MediaType.APPLICATION_JSON)
public class EnergyController {

    @Inject
    EnergyService service;

    @GET
    public List<EnergyRecord> getAllRecords() {
        Log.info("Richiesta: GET /energy");
        return service.loadDataFromCsv();
    }

    @GET
    @Path("/building-type/{type}")
    public List<EnergyRecord> filterByBuildingType(@PathParam("type") String type) {
        Log.infof("Richiesta: filtro per buildingType = %s", type);
        return service.filterByBuildingType(type);
    }

    @GET
    @Path("/average")
    public double averageConsumption() {
        Log.info("Richiesta: consumo medio totale");
        return service.getAverageConsumption();
    }

    @GET
    @Path("/average-per-day")
    public Map<String, Double> averagePerDay() {
        Log.info("Richiesta: media per giorno della settimana");
        return service.getAverageConsumptionPerDay();
    }

    @GET
    @Path("/surface-range")
    public List<EnergyRecord> surfaceRange(@QueryParam("min") int min, @QueryParam("max") int max) {
        Log.infof("Richiesta: superficie tra %s e %s", min, max);
        return service.filterBySurfaceRange(min, max);
    }

    @GET
    @Path("/top")
    public List<EnergyRecord> top(@QueryParam("limit") @DefaultValue("5") int limit) {
        Log.infof("Richiesta: top %s consumi", limit);
        return service.getTopByConsumption(limit);
    }

    @GET
    @Path("/temperature-above/{value}")
    public List<EnergyRecord> temperatureAbove(@PathParam("value") double value) {
        Log.infof("Richiesta: temperatura > %s", value);
        return service.filterByTemperatureAbove(value);
    }

    @GET
    @Path("/export")
    @Produces("text/csv")
    public Response exportCsv() {
        Log.info("Richiesta: esportazione CSV");
        String csv = service.getRawCsv();
        return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=\"energy-data.csv\"")
                .build();
    }
}