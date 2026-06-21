package com.estetica.resource;

import com.estetica.model.Procedimento;
import com.estetica.service.BunnyStorageService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.FileInputStream;
import java.util.List;

@Path("/api/procedimentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcedimentoResource {

    @Inject
    BunnyStorageService storageService;

    @GET
    public List<Procedimento> listarTodos() {
        return Procedimento.list("order by dataCriacao desc");
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response criar(@RestForm String titulo,
                          @RestForm String textoExplicativo,
                          @RestForm String tipo,
                          @RestForm FileUpload arquivo) {
        try {
            if (arquivo == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Arquivo obrigatório").build();
            }

            // 1. Upload para o Bunny.net
            FileInputStream fis = new FileInputStream(arquivo.uploadedFile().toFile());
            String urlMidia = storageService.uploadFile(fis, arquivo.fileName());

            // 2. Salva no banco de dados
            Procedimento p = new Procedimento();
            p.titulo = titulo;
            p.textoExplicativo = textoExplicativo;
            p.tipo = tipo.toUpperCase();
            p.urlMidia = urlMidia;
            p.persist();

            return Response.status(Response.Status.CREATED).entity(p).build();
        } catch (Exception e) {
            return Response.serverError().entity("Erro: " + e.getMessage()).build();
        }
    }
}
