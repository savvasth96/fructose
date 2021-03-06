package fwcd.fructose.game.ai;

import fwcd.fructose.Option;
import fwcd.fructose.game.GameMove;
import fwcd.fructose.game.GameRole;
import fwcd.fructose.game.GameState;
import fwcd.fructose.swing.TreePlotter;
import fwcd.fructose.time.Timer;

/**
 * A monte-carlo-tree-search. It doesn't need
 * a domain-specific heuristic and thus is very generally applicable.
 * 
 * @author Fredrik
 *
 */
public class MCTS<M extends GameMove, R extends GameRole> extends TemplateGameAI<M, R> {
	private Option<TreePlotter> plotter = Option.empty();

	public void setPlotter(TreePlotter plotter) {
		this.plotter = Option.of(plotter);
	}
	
	/**
	 * Selects a move using monte-carlo-tree search. Note that
	 * this method will block "forever", if not an appropriate
	 * time limit is provided.
	 */
	@Override
	protected M selectMove(
			GameState<M, R> game,
			long softMaxTime
	) {
		Timer timer = new Timer();
		timer.start(softMaxTime);
		
		MCTSNode<M, R> node = new MCTSNode<>(game.getCurrentRole(), game);
		
		while (timer.isRunning()) {
			node.performIteration();
		}
		
		plotter.ifPresent(plotter -> plotter.setTree(node));
		
		return node.mostExploredChild().getMove();
	}
}
