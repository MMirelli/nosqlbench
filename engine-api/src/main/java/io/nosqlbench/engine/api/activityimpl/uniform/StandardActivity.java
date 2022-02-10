package io.nosqlbench.engine.api.activityimpl.uniform;

import io.nosqlbench.engine.api.activityapi.planning.OpSequence;
import io.nosqlbench.engine.api.activityimpl.ActivityDef;
import io.nosqlbench.engine.api.activityimpl.OpDispenser;
import io.nosqlbench.engine.api.activityimpl.OpMapper;
import io.nosqlbench.engine.api.activityimpl.SimpleActivity;
import io.nosqlbench.engine.api.activityimpl.uniform.flowtypes.Op;
import io.nosqlbench.nb.api.errors.OpConfigError;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This is a typed activity which is expected to be come the standard
 * core of all new activity types. Extant NB drivers should also migrate
 * to this when possible.
 *
 * @param <R> A type of runnable which wraps the operations for this type of driver.
 * @param <S> The context type for the activity, AKA the 'space' for a named driver instance and its associated object graph
 */
public class StandardActivity<R extends Op, S> extends SimpleActivity {

    private final DriverAdapter<R, S> adapter;
    private final OpSequence<OpDispenser<? extends R>> sequence;

    public StandardActivity(DriverAdapter<R, S> adapter, ActivityDef activityDef) {
        super(activityDef);
        this.adapter = adapter;

        try {
            OpMapper<R> opmapper = adapter.getOpMapper();
            Function<Map<String, Object>, Map<String, Object>> preprocessor = adapter.getPreprocessor();
            sequence = createOpSourceFromCommands(opmapper, adapter.getConfiguration(), List.of(preprocessor));
        } catch (Exception e) {
            if (e instanceof OpConfigError) {
                throw e;
            } else {
                throw new OpConfigError("Error mapping workload template to operations: " + e.getMessage(), null, e);
            }
        }
    }

    @Override
    public void initActivity() {
        super.initActivity();
        setDefaultsFromOpSequence(sequence);
    }

    public OpSequence<OpDispenser<? extends R>> getOpSequence() {
        return sequence;
    }

    /**
     * When an adapter needs to identify an error uniquely for the purposes of
     * routing it to the correct error handler, or naming it in logs, or naming
     * metrics, override this method in your activity.
     *
     * @return A function that can reliably and safely map an instance of Throwable to a stable name.
     */
    @Override
    public final Function<Throwable, String> getErrorNameMapper() {
        return adapter.getErrorNameMapper();
    }

}
