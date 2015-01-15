package com.example.app_4;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import rajawali.Camera;
import rajawali.Object3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.Texture;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;

public class RajawaliBasicExampleRenderer extends RajawaliRenderer {
	private DirectionalLight mLight;
	private Object3D mSphere;
	private Context context;
	private Camera camera;

	public RajawaliBasicExampleRenderer(Context context) {
		super(context);
		this.context =context;
		setFrameRate(60);
 	}

	protected void initScene() {
		mLight = new DirectionalLight(1f, 0.2f, -1.0f); // set the direction
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(2);

		Material material = new Material();
        try {
			material.addTexture(new Texture("earthColors", R.drawable.earthtruecolor_nasa_big));
		} catch (TextureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mSphere = new Sphere(1, 18, 18);
		mSphere.setMaterial(material);
		getCurrentScene().addLight(mLight);

		getCurrentScene().addChild(mSphere);

		getCurrentCamera().setZ(4.2f);
		//getCurrentCamera().setX(1.2f);
		//getCurrentCamera().setY(3.2f);
	
	}
	


	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		mSphere.setRotY(mSphere.getRotY() + 1);
		
	}
}
