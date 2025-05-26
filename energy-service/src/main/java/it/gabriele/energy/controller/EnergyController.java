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

        Log.info("Inizio metodo getAllRecords()");

        return service.getAllFromMemory();
    }

    @GET
    @Path("/building-type/{type}")
    public List<EnergyRecord> filterByBuildingType(@PathParam("type") String type) {

        Log.info("Inizio metodo filterByBuildingType()");

        return service.filterByBuildingTypeFromMemory(type);
    }

    @GET
    @Path("/average")
    public double averageConsumption() {

        Log.info("Inizio metodo averageConsumption()");

        return service.getAverageConsumptionFromMemory();
    }

    @GET
    @Path("/average-per-day")
    public Map<String, Double> averagePerDay() {

        Log.info("Inizio metodo averagePerDay()");

        return service.getAverageConsumptionPerDayFromMemory();
    }

    @GET
    @Path("/surface-range")
    public List<EnergyRecord> surfaceRange(@QueryParam("min") int min, @QueryParam("max") int max) {

        Log.info("Inizio metodo surfaceRange()");

        return service.filterBySurfaceRangeFromMemory(min, max);
    }

    @GET
    @Path("/top")
    public List<EnergyRecord> top(@QueryParam("limit") @DefaultValue("5") int limit) {

        Log.info("Inizio metodo top()");

        return service.getTopByConsumptionFromMemory(limit);
    }

    @GET
    @Path("/temperature-above/{value}")
    public List<EnergyRecord> temperatureAbove(@PathParam("value") double value) {

        Log.info("Inizio metodo temperatureAbove()");

        return service.filterByTemperatureAboveFromMemory(value);
    }

    @GET
    @Path("/export")
    @Produces("text/csv")
    public Response exportCsv() {

        Log.info("Inizio metodo exportCsv()");

        String csv = service.getRawCsv();

        return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=\"energy-data.csv\"")
                .build();
    }
}