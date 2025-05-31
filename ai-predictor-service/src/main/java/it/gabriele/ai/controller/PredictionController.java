package it.gabriele.ai.controller;

import it.gabriele.ai.model.PredictionResult;
import it.gabriele.ai.service.PredictionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.List;

@Path("/predict")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PredictionController {

    @Inject
    PredictionService predictionService;

    @GET
    @Path("/{datetime}")
    public PredictionResult predictSingle(@PathParam("datetime") String datetime) {
        LocalDateTime date = LocalDateTime.parse(datetime);
        return predictionService.predict(date);
    }

    @POST
    public List<PredictionResult> predictBatch(List<String> datetimes) {
        List<LocalDateTime> dates = datetimes.stream().map(LocalDateTime::parse).toList();
        return predictionService.predictBatch(dates);
    }
}