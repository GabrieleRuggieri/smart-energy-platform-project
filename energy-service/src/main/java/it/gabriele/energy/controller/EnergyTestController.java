package it.gabriele.energy.controller;

import io.quarkus.logging.Log;
import it.gabriele.energy.model.EnergyTestModel;
import it.gabriele.energy.service.EnergyTestService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class EnergyTestController {

    @Inject
    EnergyTestService energyTestService;

    @GET
    @Path("/start")
    public Response startSimulation() {
        energyTestService.startScheduledSimulation();
        return Response.ok("Simulazione sensori avviata").build();
    }

    @GET
    @Path("/records")
    public List<EnergyTestModel> getAllRecords() {

        Log.info("Inizio metodo getAllRecords()");

        return energyTestService.getAllFromMemory();
    }

    @GET
    @Path("/building-type/{type}")
    public List<EnergyTestModel> filterByBuildingType(@PathParam("type") String type) {

        Log.info("Inizio metodo filterByBuildingType()");

        return energyTestService.filterByBuildingTypeFromMemory(type);
    }

    @GET
    @Path("/average")
    public double averageConsumption() {

        Log.info("Inizio metodo averageConsumption()");

        return energyTestService.getAverageConsumptionFromMemory();
    }

    @GET
    @Path("/average-per-day")
    public Map<String, Double> averagePerDay() {

        Log.info("Inizio metodo averagePerDay()");

        return energyTestService.getAverageConsumptionPerDayFromMemory();
    }

    @GET
    @Path("/surface-range")
    public List<EnergyTestModel> surfaceRange(@QueryParam("min") int min, @QueryParam("max") int max) {

        Log.info("Inizio metodo surfaceRange()");

        return energyTestService.filterBySurfaceRangeFromMemory(min, max);
    }

    @GET
    @Path("/top")
    public List<EnergyTestModel> top(@QueryParam("limit") @DefaultValue("5") int limit) {

        Log.info("Inizio metodo top()");

        return energyTestService.getTopByConsumptionFromMemory(limit);
    }

    @GET
    @Path("/temperature-above/{value}")
    public List<EnergyTestModel> temperatureAbove(@PathParam("value") double value) {

        Log.info("Inizio metodo temperatureAbove()");

        return energyTestService.filterByTemperatureAboveFromMemory(value);
    }

    @GET
    @Path("/export")
    @Produces("text/csv")
    public Response exportCsv() {

        Log.info("Inizio metodo exportCsv()");

        String csv = energyTestService.getRawCsv();

        return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=\"energy-data.csv\"")
                .build();
    }
}