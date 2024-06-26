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
package com.epagagames.particles;

import com.jme3.export.*;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import java.io.IOException;

/**
 * Emitter Shape
 * This abstract class defines some base attributes that all emitter shapes use.
 *
 * @author Jeddic
 */
public abstract class EmitterShape implements Savable, Cloneable {

  protected float randomDirection = 0.0f;
  protected float originDirection = 0.0f;
  protected float randomizePosition = 0.0f;


  protected transient Vector3f nextDirection = new Vector3f();
  protected transient Vector3f nextPosition = new Vector3f();
  protected transient Vector3f tempVec = new Vector3f();

  public abstract void setNext();
  public abstract void setNext(int index);
  public abstract int getIndex();
  public abstract Vector3f getNextTranslation();
  public abstract Vector3f getRandomTranslation();
  public abstract Vector3f getNextDirection();

  public abstract Spatial getDebugShape(Material mat, boolean ignoreTransforms);

  public float getRandomDirection() {
    return randomDirection;
  }

  public void setRandomDirection(float randomDirection) {
    this.randomDirection = randomDirection;
    if (randomDirection < 0) randomDirection = 0.0f;
    if (randomDirection > 1.0f) randomDirection = 1.0f;
  }

  public float getOriginDirection() {
    return originDirection;
  }

  public void setOriginDirection(float originDirection) {
    this.originDirection = originDirection;
    if (originDirection < 0) originDirection = 0.0f;
    if (originDirection > 1.0f) originDirection = 1.0f;
  }

  public float getRandomizePosition() {
    return randomizePosition;
  }

  public void setRandomizePosition(float randomizePosition) {
    this.randomizePosition = randomizePosition;
  }

  protected void applyRootBehaviors() {
    if (randomizePosition > 0) {
      nextPosition.add((randomizePosition * 2.0f * (FastMath.nextRandomFloat() - 0.5f)),
          (randomizePosition * 2.0f * (FastMath.nextRandomFloat() - 0.5f)),
          (randomizePosition * 2.0f * (FastMath.nextRandomFloat() - 0.5f)));
    }

    if (randomDirection > 0) {
      tempVec.set((2.0f * (FastMath.nextRandomFloat() - 0.5f)),
          (2.0f * (FastMath.nextRandomFloat() - 0.5f)),
          (2.0f * (FastMath.nextRandomFloat() - 0.5f)));
      nextDirection.x = nextDirection.x * (1.0f - randomDirection) + randomDirection * tempVec.x;
      nextDirection.y = nextDirection.y * (1.0f - randomDirection) + randomDirection * tempVec.y;
      nextDirection.z = nextDirection.z * (1.0f - randomDirection) + randomDirection * tempVec.z;
    }

    if (originDirection > 0) {
      tempVec.set(nextPosition);
      tempVec.normalizeLocal();

      nextDirection.x = nextDirection.x * (1.0f - originDirection) + originDirection * tempVec.x;
      nextDirection.y = nextDirection.y * (1.0f - originDirection) + originDirection * tempVec.y;
      nextDirection.z = nextDirection.z * (1.0f - originDirection) + originDirection * tempVec.z;
    }
  }


  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule oc = ex.getCapsule(this);
    oc.write(randomDirection, "randomdirection", 0.0f);
    oc.write(originDirection, "orgindirection", 0.0f);
    oc.write(randomizePosition, "randomizeposition", 0.0f);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule ic = im.getCapsule(this);
    randomDirection = ic.readFloat("randomdirection", 0.0f);
    originDirection = ic.readFloat("orgindirection", 0.0f);
    randomizePosition = ic.readFloat("randomizeposition", 0.0f);

  }

  @Override
  public EmitterShape clone() {
    try {
      EmitterShape clone = (EmitterShape) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  public boolean equals(Object o) {
    if (!(o instanceof EmitterShape)) return false;

    EmitterShape check = (EmitterShape)o;

    if (randomDirection != check.randomDirection) return false;
    if (originDirection != check.originDirection) return false;
    if (randomizePosition != check.randomizePosition) return false;


    return true;
  }

}
