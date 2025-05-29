package it.gabriele.energy.controller;

import it.gabriele.energy.model.EnergyBrazilModel;
import it.gabriele.energy.service.EnergyBrazilService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/brazil")
@Produces(MediaType.APPLICATION_JSON)
public class EnergyBrazilController {

    @Inject
    EnergyBrazilService service;

    @GET
    @Path("/start")
    public Response start() {
        service.startSimulation();
        return Response.ok("Simulazione avviata").build();
    }

    @GET
    @Path("/all")
    public List<EnergyBrazilModel> getAll() {
        return service.getAllFromMemory();
    }

    @GET
    @Path("/average")
    public double getAverage() {
        return service.getAverageConsumption();
    }

    @GET
    @Path("/max")
    public EnergyBrazilModel getMax() {
        return service.getMaxRecord();
    }

    @GET
    @Path("/total-per-day")
    public Map<String, Double> getDailyTotals() {
        return service.getDailyTotals().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));
    }

    @GET
    @Path("/between")
    public List<EnergyBrazilModel> getBetween(@QueryParam("from") String from, @QueryParam("to") String to) {
        LocalDateTime fromDate = LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime toDate = LocalDateTime.parse(to, DateTimeFormatter.ISO_DATE_TIME);
        return service.filterByDateRange(fromDate, toDate);
    }

    @GET
    @Path("/export")
    @Produces("text/csv")
    public Response exportCsv() {
        String csv = service.exportCsv();
        return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=\"brazil-energy.csv\"")
                .build();
    }
}