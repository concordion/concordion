package spec.concordion.command.run;

import java.util.LinkedHashMap;
import java.util.Map;

import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.Runner;
import org.concordion.api.RunnerResult;

public class ExampleRunner implements Runner {

    private static Map<String, String> mappings = new LinkedHashMap<String, String>();
    
    public RunnerResult execute(Resource resource, String href) throws Exception {
        
        for (String regex : mappings.keySet()) {
            if (href.matches(regex)) {
                return new RunnerResult(Result.valueOf(mappings.get(regex)));
            }
        }
        
        throw new RuntimeException("No mapping found for '" + href + "'.");
    }

    public static void clear() {
        mappings.clear();
    }

    public static void addMapping(String regex, String result) {
        mappings.put(regex, result);
    }
}
