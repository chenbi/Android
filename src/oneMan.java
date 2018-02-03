
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;


/**
 *
 * @author Administrator
 */
public class oneMan 
{
    // static dimensions    
    private static final float BODY_LENGTH=3.0f;
    private static final float BODY_RADIUS=1.5f;
    private static final float LIMB_LENGTH=1.7f;
    private static final float LIMB_RADIUS=0.4f;
    private static final float HORN_LENGTH=0.6f;
    private static final float HORN_RADIUS=0.1f;
    
    // drawing precision
    private static final int SLICES=30;
    private static final int STACKS=30;    
     
    // animation    
    private static  float ARM_SWING=0.0f;
    private static  float DELTA_ARM_SWING=5.0f;
    private static  float MAX_ARM_SWING=50.0f;
    
    private static  float TALK_SWING=0.0f;
    private static  float MAX_TALK_SWING=5.0f;
    private static  float DELTA_TALK_SWING=2.0f;

    public oneMan()
    {
       
    }

    public void drawMe(GL gl)
    {
        // assume origo in bodys center.
        // x:forward, y:twds horses left, z:up

        GLU glu=new GLU();
        GLUquadric q=glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(q, GLU.GLU_FILL);
                
        //glu.gluQuadricDrawStyle(q, GLU.GLU_SILHOUETTE);
        glu.gluQuadricOrientation(q, GLU.GLU_OUTSIDE);
        glu.gluQuadricNormals(q, GLU.GLU_SMOOTH);

        // set the material
        setGreenMaterial(gl);
        

        
        //---------------------------
        // draw body
        gl.glPushMatrix();
        glu.gluCylinder(q, BODY_RADIUS, BODY_RADIUS, BODY_LENGTH, SLICES, STACKS);
        glu.gluDisk(q, 0.0f, BODY_RADIUS, SLICES, STACKS);
        gl.glTranslatef(0.0f, 0.0f, BODY_LENGTH);
        glu.gluDisk(q, 0.0f, BODY_RADIUS, SLICES, STACKS);
	
        // draw head top 
        // talk
        gl.glRotatef(TALK_SWING, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, 0.07f+0.05f*TALK_SWING);
        glu.gluDisk(q, 0.0f, BODY_RADIUS, SLICES, STACKS);
        
        // halfsphere
        double[] cutplane=new double[]{0.0f,0.0f,1.0f,0.0f};
        gl.glClipPlane(GL.GL_CLIP_PLANE2,cutplane,0);
        gl.glEnable(GL.GL_CLIP_PLANE2);

        glu.gluSphere(q, BODY_RADIUS, SLICES, STACKS);
        
        gl.glDisable(GL.GL_CLIP_PLANE2);
        
         //horn
        drawHorn(gl,glu,q);
        
        // eyes
        setBlackMaterial(gl);
        drawEyes(gl,glu,q);
        setGreenMaterial(gl);
        
        // Draw arms
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(BODY_RADIUS+LIMB_RADIUS+0.1f, 0.0f, BODY_LENGTH-LIMB_RADIUS);
        
        drawLimb(gl,glu,q,-ARM_SWING);
        
        gl.glPopMatrix();
        gl.glPushMatrix(); 
        gl.glTranslatef(-(BODY_RADIUS+LIMB_RADIUS+0.1f), 0.0f, BODY_LENGTH-LIMB_RADIUS);
        
        drawLimb(gl,glu,q,ARM_SWING);
        
        // Draw legs
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(BODY_RADIUS-LIMB_RADIUS*2.0f, 0.0f, 0.2f);
        
        drawLimb(gl,glu,q,ARM_SWING);
        
        
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(-(BODY_RADIUS-LIMB_RADIUS*2.0f), 0.0f, 0.2f);
        
        drawLimb(gl,glu,q,-ARM_SWING);       
        
        gl.glPopMatrix();
 
        // delete the quadric
        glu.gluDeleteQuadric(q);

    }
    
    private void drawLimb(GL gl,GLU glu,GLUquadric q,float swing)
    {
        // arm or leg
        gl.glPushMatrix();
        gl.glRotatef(swing,1.0f,0.0f,0.0f);
        glu.gluSphere(q, LIMB_RADIUS, SLICES, STACKS);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glRotatef(180.0f+swing,1.0f,0.0f,0.0f);
        glu.gluCylinder(q, LIMB_RADIUS, LIMB_RADIUS, LIMB_LENGTH, SLICES, STACKS);
        gl.glTranslated(0.0f, 0.0f, LIMB_LENGTH);
        glu.gluSphere(q, LIMB_RADIUS, SLICES, STACKS);       
        gl.glPopMatrix();
    }
    
    private void drawEyes(GL gl,GLU glu,GLUquadric q)
    {
        gl.glPushMatrix();
        gl.glRotatef(-20.0f,1.0f,0.0f,0.0f);
        gl.glRotatef(70.0f,0.0f,1.0f,1.0f);
        gl.glTranslated(0.0f, 0.0f, BODY_RADIUS -0.8f*LIMB_RADIUS);
        glu.gluSphere(q, LIMB_RADIUS, SLICES, STACKS);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glRotatef(-20.0f,1.0f,0.0f,0.0f);
        gl.glRotatef(-70.0f,0.0f,1.0f,1.0f);
        gl.glTranslated(0.0f, 0.0f, BODY_RADIUS -0.8f*LIMB_RADIUS);
        glu.gluSphere(q, LIMB_RADIUS, SLICES, STACKS);
        gl.glPopMatrix();       
        gl.glPopMatrix();
    }

    private void drawHorn(GL gl,GLU glu,GLUquadric q)
    {
        gl.glPushMatrix();
        gl.glRotatef(10.0f,1.0f,0.0f,0.0f);
        gl.glRotatef(30.0f,0.0f,1.0f,1.0f);
        gl.glTranslatef(0.0f, 0.0f, BODY_RADIUS-0.1f);
        glu.gluCylinder(q, HORN_RADIUS, HORN_RADIUS, HORN_LENGTH, SLICES, STACKS);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glRotatef(10.0f,1.0f,0.0f,0.0f);
        gl.glRotatef(-30.0f,0.0f,1.0f,1.0f);
        gl.glTranslatef(0.0f, 0.0f, BODY_RADIUS-0.1f);
        glu.gluCylinder(q, HORN_RADIUS, HORN_RADIUS, HORN_LENGTH, SLICES, STACKS);
        gl.glPopMatrix();    
    }


    public void setGreenMaterial(GL gl)
    {
    	//Emaraldish
        float amb[]={0.0215f, 0.1745f, 0.0215f, 0.55f };
        float diff[]={0.07568f, 0.61424f, 0.07568f, 0.55f};
        float spec[]={0.633f, 0.727811f, 0.633f, 0.55f};
        float shine=76.8f;

        gl.glMaterialfv(GL.GL_FRONT_AND_BACK,GL.GL_AMBIENT,amb,0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK,GL.GL_DIFFUSE,diff,0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK,GL.GL_SPECULAR,spec,0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK,GL.GL_SHININESS,shine);
    }
    
    public void setBlackMaterial(GL gl)
    {
    	//black plastic
        float amb[]={0.0f, 0.0f, 0.0f, 1.0f};
        float diff[]={0.01f, 0.01f, 0.01f, 1.0f};
        float spec[]={0.50f, 0.50f, 0.50f, 1.0f };
        float shine=32.0f;

        gl.glMaterialfv(GL.GL_FRONT_AND_BACK,GL.GL_AMBIENT,amb,0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK,GL.GL_DIFFUSE,diff,0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK,GL.GL_SPECULAR,spec,0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK,GL.GL_SHININESS,shine);
    }

    public void updateAngles(boolean walk, boolean talk)
    {        
        if(walk)
        {
            ARM_SWING+=DELTA_ARM_SWING;
            if((ARM_SWING > MAX_ARM_SWING)||(ARM_SWING < - MAX_ARM_SWING))
                DELTA_ARM_SWING = -DELTA_ARM_SWING;
        }
        
        if(talk)
        {
            TALK_SWING+=DELTA_TALK_SWING;
            if((TALK_SWING > MAX_TALK_SWING) ||(TALK_SWING < 0 ))
                DELTA_TALK_SWING = -DELTA_TALK_SWING;
        }
    }    
}