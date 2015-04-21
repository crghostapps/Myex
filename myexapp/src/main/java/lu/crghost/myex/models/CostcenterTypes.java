package lu.crghost.myex.models;

import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static cost center types
 */
public class CostcenterTypes {

    public static List<CostcenterType> CCTYPES = new ArrayList<CostcenterType>();
    public static Map<String, CostcenterType> CCTYPES_MAP = new HashMap<String, CostcenterType>();

    static {
        String[] sa = MyExApp.getContext().getResources().getStringArray(R.array.costcenter_type_array);
        for (int i=0; i<sa.length;i++) {
            addItem(new CostcenterType(Integer.toString(i), sa[i]));
        }
    }

    private static void addItem(CostcenterType item) {
        CCTYPES.add(item);
        CCTYPES_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class CostcenterType {
        public String id;
        public String name;

        public CostcenterType(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
