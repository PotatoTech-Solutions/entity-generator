package com.potatotech.entitygenerator.service.java;

import com.potatotech.entitygenerator.model.Entities;
import com.potatotech.entitygenerator.service.common.FieldsMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static com.potatotech.entitygenerator.service.common.Common.*;

public class GenerateHandler {

    protected static void generateHandlerEntities(List<Entities> entities, String packageName, Path packagePath){

        String mod = loadWxsd("handlerentities");
        entities.forEach(item -> {
            try{
                if(item.isGenerateDefaultHandlers()){
                    String fileName = stringFormaterJava(item.getEntityName(),"Handler", packagePath.toString());
                    var path = Path.of(fileName);
                    var entity = configureFile(mod,packageName,item,item.getEntityName());
                    Files.write(path, entity.getBytes(), StandardOpenOption.CREATE);
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        });
    }

    private static String configureFile(String mod,String packageName, Entities entity,String fileName){

        var fieldTypeIdentity = entity.getEntityFields().stream().filter(item -> item.getMetadata().isKey()).findFirst().orElse(null);
        var typeField = fieldTypeIdentity.getFieldProperties().getFieldType();
        var fieldType = FieldsMapper.getFieldTypeEntity(typeField);
        return mod.replace("<<entityName>>",firstCharacterUpperCase(fileName))
                .replace("<<entityNameLowerCase>>",fileName)
                .replace("<<keyType>>", fieldType)
                .replace("<<packageName>>",packageName.concat("_gen"));
    }

    protected static void generateHandlers(List<Entities> entities, String packageName, Path packagePath){

        String mod = loadWxsd("handlerentities");
        entities.forEach(item -> {
            try{
                String fileName = stringFormaterJava(item.getEntityName(),"Handler", packagePath.toString());
                var path = Path.of(fileName);
                var entity = configureFile(mod,packageName,item,item.getEntityName());
                Files.write(path, entity.getBytes(), StandardOpenOption.CREATE);
            }catch (IOException ex){
                ex.printStackTrace();
            }
        });
    }
}
