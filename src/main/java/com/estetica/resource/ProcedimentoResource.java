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

        // 🔍 DEBUG 1: Imprime os textos que vieram do Vue.js
        System.out.println("=== DEBUG FRONTEND ===");
        System.out.println("Titulo recebido: " + titulo);
        System.out.println("Tipo recebido: " + tipo);
        System.out.println("Texto recebido: " + textoExplicativo);

        // 🔍 DEBUG 2: Verifica se o arquivo chegou nulo ou preenchido
        if (arquivo == null) {
            System.out.println("Arquivo chegou: NULO (O Vue nao enviou o binario corretamente)");
        } else {
            System.out.println("Arquivo chegou: OK");
            System.out.println("Nome do arquivo: " + arquivo.fileName());
            System.out.println("Tamanho do arquivo: " + arquivo.size() + " bytes");
        }

        try {
            if (arquivo == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Arquivo obrigatório").build();
            }

            FileInputStream fis = new FileInputStream(arquivo.uploadedFile().toFile());

            // 🔍 DEBUG 3: Tenta disparar o upload para a Bunny
            System.out.println("Iniciando transmissao para Bunny.net...");
            String urlMidia = storageService.uploadFile(fis, arquivo.fileName());
            System.out.println("Upload concluido! URL gerada: " + urlMidia);

            Procedimento p = new Procedimento();
            p.titulo = titulo;
            p.textoExplicativo = textoExplicativo;
            p.tipo = tipo.toUpperCase();
            p.urlMidia = urlMidia;

            System.out.println("Salvando relatorio no Supabase...");
            p.persist();
            System.out.println("Gravacao concluida com sucesso no Supabase!");

            return Response.status(Response.Status.CREATED).entity(p).build();
        } catch (Exception e) {
            // 🔍 DEBUG 4: Se quebrar, imprime a pilha de erro inteira no terminal do Google
            System.out.println("=== EXCEÇÃO ENCONTRADA ===");
            e.printStackTrace();
            return Response.serverError().entity("Erro: " + e.getMessage()).build();
        }
    }

}
