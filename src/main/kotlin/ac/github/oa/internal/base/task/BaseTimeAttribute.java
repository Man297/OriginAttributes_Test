package ac.github.oa.internal.base.task;

import java.util.List;

public class BaseTimeAttribute {

    public List<String> list;
    public Long stamp;

    public BaseTimeAttribute(List<String> list, Long stamp) {
        this.list = list;
        this.stamp = System.currentTimeMillis() + stamp * 1000;;
    }
}
