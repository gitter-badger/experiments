package nl.yeex.knowledge.offline.generators;

import java.util.HashMap;
import java.util.Map;

import nl.yeex.knowledge.core.adapters.DataPropertyVO;
import nl.yeex.knowledge.core.adapters.OntologyVO;
import nl.yeex.knowledge.core.generation.FreemarkerI18nHelper;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 *
 */
public class DataPropertiesGenerator implements Generator {
    private static final int LINE_MAXIMUM = 40;

    public void generate(GeneratorContext context, OWLOntology ontology) {
        OntologyVO ontologyVO = new OntologyVO(ontology);
        context.log("\nExport data properties\n");
        int counter = 0;
        for (DataPropertyVO dataProperty : ontologyVO.getDataProperties()) {
            context.log("%");
            if (++counter % LINE_MAXIMUM == 0) {
                context.log("\n");
            }
            // SETUP TEMPLATE MODEL
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("dataProperty", dataProperty);
            model.put("ontology", ontologyVO);
            model.put("context", context);
            model.put("messages", new FreemarkerI18nHelper(context.getLocale()));

            String outputFile = dataProperty.getPropertyLabel() + ".html";

            // DETERMINE TEMPLATE:
            String templateFile = "__dataproperty.ftl"; // default;

            String specificTemplate = "dataproperty_" + dataProperty.getPropertyLabel() + ".ftl";
            if (context.templateExists(specificTemplate)) {
                templateFile = specificTemplate;
            }

            // GENERATE
            context.generate(templateFile, model, outputFile);
        }
    }

}
