/*
 * Copyright (c) 2019 Greg Hoffman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.epagagames.particles.emittershapes;

import com.epagagames.particles.EmitterShape;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;

import java.io.IOException;

/**
 * Emitter Circle
 * Emits particles from a circle shape. Particles are emitted with a default velocity pointed away from the center
 * of the circle.
 *
 * @author Jeddic
 */
public class EmitterCircle extends EmitterShape {

  private float radius = 1.0f;
  private float arc = FastMath.PI * 2.0f;
  private float radiusThickness = 1.0f;

  private transient Vector3f nextDirection = new Vector3f();
  private transient Vector3f nextPosition = new Vector3f();
  private transient Quaternion temp = new Quaternion();

  public EmitterCircle() {

  }

  @Override
  public Spatial getDebugShape(Material mat, boolean ignoreTransforms) {
    Cylinder cylinder = new Cylinder(2, 20, radius, 0.1f, false);
    Geometry geometry = new Geometry("DebugShape", cylinder);
    geometry.setMaterial(mat);
    geometry.rotate(FastMath.PI / 2.0f, 0, 0);
    //geometry.setIgnoreTransform(ignoreTransforms);
    return geometry;
  }


  @Override
  public void setNext() {
    nextPosition.set(1, 0, 0);
    temp.set(Quaternion.IDENTITY);
    temp.fromAngleAxis(arc * FastMath.nextRandomFloat(), Vector3f.UNIT_Y);
    temp.mult(nextPosition, nextPosition);

    // now generate length
    float v = FastMath.nextRandomFloat();
    float len = radius * (v * radiusThickness + (1 - radiusThickness));
    nextPosition.multLocal(len);

    nextDirection.set(2.0f * (FastMath.nextRandomFloat() - 0.5f),
                      0,
                      2.0f * (FastMath.nextRandomFloat() - 0.5f));

    nextDirection.normalizeLocal();

    applyRootBehaviors();
  }

  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public float getArc() {
    return arc;
  }

  public void setArc(float arc) {
    this.arc = arc;
  }

  public float getRadiusThickness() {
    return radiusThickness;
  }

  public void setRadiusThickness(float radiusThickness) {
    this.radiusThickness = radiusThickness;
  }

  @Override
  public void setNext(int index) {
    setNext();
  }

  @Override
  public int getIndex() {
    return -1;
  }

  @Override
  public Vector3f getNextTranslation() {
    return nextPosition;
  }

  @Override
  public Vector3f getRandomTranslation() {
    return nextPosition;
  }

  @Override
  public Vector3f getNextDirection() {
    return nextDirection;
  }

  @Override
  public void write(JmeExporter ex) throws IOException {
    super.write(ex);
    OutputCapsule oc = ex.getCapsule(this);
    oc.write(radius, "radius", 1.0f);
    oc.write(radiusThickness, "radiusthickness", 1.0f);
    oc.write(arc, "arc", FastMath.PI);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    super.read(im);
    InputCapsule ic = im.getCapsule(this);
    radius = ic.readFloat("radius", 1.0f);
    radiusThickness = ic.readFloat("radiusthickness", 1.0f);
    arc = ic.readFloat("arc", FastMath.PI);

  }

  @Override
  public EmitterCircle clone() {
    try {
      EmitterCircle clone = (EmitterCircle) super.clone();
      return clone;
    } catch (Exception e) {
      throw new AssertionError();
    }
  }


  public boolean equals(Object o) {
    if (!super.equals(o)) return false;
    if (!(o instanceof EmitterCircle)) return false;


    EmitterCircle check = (EmitterCircle)o;

    if (radius != check.radius) return false;
    if (radiusThickness != check.radiusThickness) return false;
    if (arc != check.arc) return false;


    return true;
  }
}
