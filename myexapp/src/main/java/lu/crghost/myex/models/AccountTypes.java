package lu.crghost.myex.models;

import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static AccountTypes
 */
public class AccountTypes {

    public static List<AccountType> ACTYPES = new ArrayList<AccountType>();
    public static Map<String, AccountType> ACTYPES_MAP = new HashMap<String, AccountType>();

    static {
        String[] sa = MyExApp.getContext().getResources().getStringArray(R.array.account_type_array);
        for (int i=0; i<sa.length;i++) {
            addItem(new AccountType(Integer.toString(i), sa[i]));
        }
    }

    private static void addItem(AccountType item) {
        ACTYPES.add(item);
        ACTYPES_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class AccountType {
        public String id;
        public String name;

        public AccountType(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
