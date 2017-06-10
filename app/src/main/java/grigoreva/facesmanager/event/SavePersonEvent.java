package grigoreva.facesmanager.event;

/**
 * Created by Лена on 10.06.2017.
 */
public class SavePersonEvent implements MessageEvent<Boolean> {
    private boolean result;

    public SavePersonEvent(boolean result) {
        this.result = result;
    }
    @Override
    public Boolean getData() {
        return result;
    }
}
