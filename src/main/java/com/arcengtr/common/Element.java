package com.arcengtr.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Element {
    private int[] nodeId;
    private Jacobian[] jacobians;
}
