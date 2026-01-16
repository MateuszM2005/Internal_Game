Implemented custom collision detection and physics, handled frame-rate independence, and managed game state transitions without external libraries. 
Also provided NPCs with a simple pathfinding and battle formation AI. This project was built from scratch, minimum libraries.

## 🔧 Technical Limitations & Future Improvements

While this engine served its purpose as an initial exploration of game architecture, I have identified several key areas for optimization typically found in high-performance systems:

1.  **Determinism & Delta Time:**
    * *Current:* Physics updates are tied to the frame rate.
    * *Improvement:* Implement a fixed time-step loop (Delta Time) to decouple physics simulation from rendering, ensuring consistent behavior across different hardware.

2.  **Pathfinding Optimization:**
    * *Current:* Basic BFS algorithm.
    * *Improvement:* For larger maps, I would implement **Jump Point Search (JPS)** or pre-calculated navigation graphs to reduce search space and memory overhead.

3.  **Rendering Pipeline:**
    * *Current:* JavaFX (Scene Graph overhead).
    * *Improvement:* Switch to a lower-level library like LWJGL/OpenGL for direct control over the rendering buffer and better handling of large sprite batches (1x1 tiles).
