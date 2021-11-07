package io.renren.modules.xxljob.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class PageQueryBaseVO<T> implements Serializable {

    private int recordsTotal;

    private int recordsFiltered;

    private List<T> data;

}
