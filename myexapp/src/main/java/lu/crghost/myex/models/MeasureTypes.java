package lu.crghost.myex.models;

import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static MeasureTypes
 */
public class MeasureTypes {

    public static List<MeasureType> MTYPES = new ArrayList<MeasureType>();
    public static Map<String, MeasureType> MTYPES_MAP = new HashMap<String, MeasureType>();

    static {
        String[] sa = MyExApp.getContext().getResources().getStringArray(R.array.measure_type_array);
        for (int i=0; i<sa.length;i++) {
            addItem(new MeasureType(Integer.toString(i), sa[i]));
        }
    }

    private static void addItem(MeasureType item) {
        MTYPES.add(item);
        MTYPES_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class MeasureType {
        public String id;
        public String name;

        public MeasureType(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
