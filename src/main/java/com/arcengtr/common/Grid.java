package com.arcengtr.common;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Grid {
    private int nN;
    private int nE;

    @Builder.Default
    private List<Node> nodes = new ArrayList<>();

    @Builder.Default
    private List<Element> elements = new ArrayList<>();
}
