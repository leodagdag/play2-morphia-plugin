package leodagdag.play2morphia.test.models;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Reference;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 15:35
 */
@Embedded
public class HalfDay {

    @Embedded
    private List<Period> periods = Lists.newArrayList();

    @Reference(ignoreMissing = true)
    public Mission mission;

    public boolean isSpecial() {
        return !this.periods.isEmpty();
    }

    public void addPeriod(Period period) {
        this.periods.add(period);
    }

}
