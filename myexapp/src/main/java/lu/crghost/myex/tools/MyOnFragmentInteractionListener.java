package lu.crghost.myex.tools;

/**
 * Callback from fragments
 */
public interface MyOnFragmentInteractionListener {

    public static final int ACTION_EDIT_ACCOUNT = 0;
    public static final int ACTION_EDIT_COSTCENTER = 1;
    public static final int ACTION_EDIT_DEBTOR = 2;
    public static final int ACTION_EDIT_TRANSACTION = 3;

    public void onFragmentInteractionEdit(String id, int action);

    /**
     * Implementated in MainFragment.java
     * @param account_id
     * @param costcenter_id
     * @param debtor_id
     * @param description
     */
    public void onFragmentInteractionNewTransaction(String account_id,
                                                    String costcenter_id,
                                                    String debtor_id,
                                                    String description
                                                    );

}
