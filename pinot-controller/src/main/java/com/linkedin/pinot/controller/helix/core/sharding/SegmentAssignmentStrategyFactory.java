package com.linkedin.pinot.controller.helix.core.sharding;

/**
 * Get SegmentAssignmentStrategyFactory methods.
 * 
 * @author xiafu
 *
 */
public class SegmentAssignmentStrategyFactory {

  public static SegmentAssignmentStrategy getSegmentAssignmentStrategy(String strategy) {
    switch (SegmentAssignmentStrategyEnum.valueOf(strategy)) {
      case BalanceNumSegmentAssignmentStrategy:
        return new BalanceNumSegmentAssignmentStrategy();
      case RandomAssignmentStrategy:
        return new BalanceNumSegmentAssignmentStrategy();
      case BucketizedSegmentAssignmentStrategy:
        return new BalanceNumSegmentAssignmentStrategy();
      default:
        throw new UnsupportedOperationException("Not support SegmentAssignmentStrategy - " + strategy);
    }
  }
}