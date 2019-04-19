package loon.opengl.d3d;

import loon.LRelease;
import loon.action.camera.BaseCamera;
import loon.opengl.light.Lights;
import loon.utils.Array;
import loon.utils.TArray;
import loon.utils.cache.Pool;

public class ModelBatch implements LRelease{
	protected BaseCamera camera;
	protected final Pool<Renderable> renderablesPool = new Pool<Renderable>() {
		@Override
		protected Renderable newObject () {
			return new Renderable();
		}

		@Override
		public Renderable obtain () {
			Renderable renderable = super.obtain();
			renderable.lights = null;
			renderable.material = null;
			renderable.mesh = null;
			renderable.shader = null;
			return renderable;
		}
	};

	protected final TArray<Renderable> renderables = new TArray<Renderable>();

	protected final Array<Renderable> reuseableRenderables = new Array<Renderable>();

	protected final RenderContext context;

	protected final ShaderProvider shaderProvider;

	protected final RenderableSorter sorter;
	
	public ModelBatch(RenderContext context, ShaderProvider shaderProvider, RenderableSorter sorter) {
		this.context = context;
		this.shaderProvider = shaderProvider;
		this.sorter = sorter;
	}
	
	public ModelBatch(ShaderProvider shaderProvider) {
		this(new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 1)),
			  shaderProvider,
			  new DefaultRenderableSorter());
	}
	
	public ModelBatch(final String vertexShader, final String fragmentShader) {
		this(new DefaultShaderProvider(vertexShader, fragmentShader));
	}
	
	public ModelBatch() {
		this(new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 1)),
			  new DefaultShaderProvider(),
			  new DefaultRenderableSorter());
	}

	public void begin (BaseCamera cam) {
		this.camera = cam;
	}

	public void end () {
		sorter.sort(camera, renderables);
		context.begin();
		Shader currentShader = null;
		for (int i = 0; i < renderables.size; i++) {
			final Renderable renderable = renderables.get(i);
			if (currentShader != renderable.shader) {
				if (currentShader != null)
					currentShader.end();
				currentShader = renderable.shader;
				currentShader.begin(camera, context);
			}
			currentShader.render(renderable);
		}
		if (currentShader != null)
			currentShader.end();
		context.end();
		renderablesPool.freeAll(reuseableRenderables);
		reuseableRenderables.clear();
		renderables.clear();
		camera = null;
	}

	public void render(final Renderable renderable) {
		renderable.shader = shaderProvider.getShader(renderable);
		renderable.mesh.setAutoBind(false);
		renderables.add(renderable);
	}
	
	public void render(final RenderableProvider renderableProvider) {
		render(renderableProvider, null, null);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders) {
		render(renderableProviders, null, null);
	}
	
	public void render(final RenderableProvider renderableProvider, final Lights lights) {
		render(renderableProvider, lights, null);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, final Lights lights) {
		render(renderableProviders, lights, null);
	}
	
	public void render(final RenderableProvider renderableProvider, final Shader shader) {
		render(renderableProvider, null, shader);
	}
	
	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, final Shader shader) {
		render(renderableProviders, null, shader);
	}

	public void render(final RenderableProvider renderableProvider, final Lights lights, final Shader shader) {
		int offset = renderables.size;
		renderableProvider.getRenderables(renderables, renderablesPool);
		for (int i = offset; i < renderables.size; i++) {
			Renderable renderable = renderables.get(i);
			renderable.lights = lights;
			renderable.shader = shader;
			renderable.shader = shaderProvider.getShader(renderable);
			reuseableRenderables.add(renderable);
		}
	}
	

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, final Lights lights, final Shader shader) {
		for (final RenderableProvider renderableProvider : renderableProviders)
			render(renderableProvider, lights, shader);
	}

	@Override
	public void close () {
		shaderProvider.close();
	}
}
