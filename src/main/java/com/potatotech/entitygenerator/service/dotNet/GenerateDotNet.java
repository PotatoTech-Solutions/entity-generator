package com.potatotech.entitygenerator.service.dotNet;

import com.google.gson.Gson;
import com.potatotech.entitygenerator.model.Properties;
import com.potatotech.entitygenerator.service.common.Common;
import com.potatotech.entitygenerator.service.common.GenerateCommon;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static com.potatotech.entitygenerator.service.common.Common.loadPath;
import static com.potatotech.entitygenerator.service.common.GenerateResources.generateResources;
import static com.potatotech.entitygenerator.service.dotNet.GenerateContext.generateDbContext;
import static com.potatotech.entitygenerator.service.dotNet.GenerateContext.generateScoped;
import static com.potatotech.entitygenerator.service.dotNet.GenerateDTO.generateDTO;
import static com.potatotech.entitygenerator.service.dotNet.GenerateEntity.generateEntity;
import static com.potatotech.entitygenerator.service.dotNet.GenerateHandler.generateHandler;
import static com.potatotech.entitygenerator.service.dotNet.GenerateHandler.generateHandlerImpl;
import static com.potatotech.entitygenerator.service.dotNet.GenerateRepository.generateIRepositories;
import static com.potatotech.entitygenerator.service.dotNet.GenerateRepository.generateRepositories;


public class GenerateDotNet {

    private static Path packagePath = null;

    public static void generateSource(Properties prop){
        // Limpa os arquivos gerados anteriomente
        dropAndCreateDir(prop.getMainPackage());

        // gera a classe das entidaeds
        generateEntity(prop.getEntities(),prop.getMainPackage(),packagePath);

        // gera as classes de DTO
        generateDTO(prop.getEntities(),prop.getMainPackage(),packagePath);

        // Gera requestData e outputData
        GenerateCommon.generateFileCommon(prop.getMainPackage(),packagePath, "requestdata", "RequestData");
        GenerateCommon.generateFileCommon(prop.getMainPackage(),packagePath, "responsedata", "ResponseData");

        // Gera IBaseRepository
        GenerateCommon.generateFileCommon(prop.getMainPackage(),packagePath, "ibaserepository", "IBaseRepository");

        // Gera DynamicSchemaModelCacheKeyFactory
        GenerateCommon.generateFileCommon(prop.getMainPackage(),packagePath, "dynamicreplaceschemafactory", "DynamicSchemaModelCacheKeyFactory");

        //Generate dbContext
        generateDbContext(prop.getEntities(),prop.getMainPackage(),packagePath);

        //Gera o scoped
        generateScoped(prop.getEntities(),prop.getMainPackage(),packagePath);

        // gera os repositories
        generateIRepositories(prop.getEntities(),prop.getMainPackage(),packagePath);
        generateRepositories(prop.getEntities(),prop.getMainPackage(),packagePath);

        //Gera os handlers
        generateHandler(prop.getEntities(),prop.getMainPackage(),packagePath);
        generateHandlerImpl(prop.getEntities(),prop.getMainPackage(),packagePath);

        //Gera os DTOConverter
        GenerateDTOConverter.generateDTOConverter(prop.getEntities(),prop.getMainPackage(),packagePath);

        //Gera as primitivas
        GenerateEndpoint.generateEndpoint(prop.getEndpoints(),prop.getMainPackage(),packagePath);

        // faz uma copia da properties_dot.json para a pasta static
        generateMetadata(prop);

        generateResources(prop.getEntities(),prop.getEndpoints());
    }

    private static void dropAndCreateDir(String packageName){
        String path = loadPath();
        String pack = packageName.replace(".","/");

        packagePath = Paths.get(String.format("%s/%s_gen",path,pack));
        Common.resourcePath = Paths.get(String.format("%s/static",path));
        try {
            dropFiles(packagePath);
            Files.deleteIfExists(packagePath);
            Files.createDirectories(packagePath);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private static void generateMetadata(Properties prop){

        try{
            String fileName = String.format("%s/properties.json", Common.resourcePath.toString());
            Common.dropFile(fileName);
            var path = Path.of(fileName);
            var entity = new Gson().toJson(prop);
            Files.write(path, entity.getBytes(), StandardOpenOption.CREATE);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private static void dropFiles(Path path){

        try{
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println("delete file " +file.toAbsolutePath());
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (IOException ex){
        }
    }
}
