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
package com.epagagames.particles.valuetypes;

import com.jme3.export.*;
import com.jme3.math.ColorRGBA;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Gradient
 * This represents a gradient which can be used with the particle system
 *
 * @author Jeddic
 */
public class Gradient implements Savable, Cloneable {

  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule oc = ex.getCapsule(this);
    GradPoint[] pointArray = points.toArray(new GradPoint[points.size()]);
    oc.write(pointArray, "points", new GradPoint[]{});
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule ic = im.getCapsule(this);
    Savable[] pointArray = (Savable[]) ic.readSavableArray("points", new GradPoint[]{});
    for (int i = 0; i < pointArray.length; i++) {
      points.add((GradPoint) pointArray[i]);
    }
  }

  private ArrayList<GradPoint> points = new ArrayList<>();

  public Gradient() {

  }

  public Gradient(ColorRGBA color) {
    points.add(new GradPoint(color, 0.0f));
  }

  public Gradient addGradPoint(ColorRGBA color, float x) {
    points.add(new GradPoint(color, x));
    sort();
    return this;
  }

  public int getSize() {
    return points.size();
  }

  public GradPoint getPoint(int index) {
    return points.get(index);
  }

  public void sort() {
    points.sort((c1, c2)->{
      if (c1.x < c2.x) return -1;
      else if (c1.x > c2.x) return 1;
      else return 0;
    });

  }

  public ColorRGBA getValueColor(float percent, ColorRGBA store) {
    // find which points we are in between
    GradPoint lastPoint = null;
    GradPoint currentPoint = null;
    for (int i = 0; i < points.size(); i++) {
      lastPoint = currentPoint;
      currentPoint = points.get(i);

      if (currentPoint.x >= percent) {
        // now get the interpolated value
        if (lastPoint == null && currentPoint != null) {
          // just use the current points y value
          if (store == null) {
            return new ColorRGBA(currentPoint.color);
          } else {
            store.set(currentPoint.color);
            return store;
          }
        } else if (lastPoint != null && currentPoint != null) {
          // Calculate the percent distance we are in between the two points
          float perc = (percent - lastPoint.x) / (currentPoint.x - lastPoint.x);

          if (store == null) {
            return new ColorRGBA().interpolateLocal(lastPoint.color, currentPoint.color, perc);
          } else {
            store.interpolateLocal(lastPoint.color, currentPoint.color, perc);
            return store;
          }
        }
      }
    }

    // we must be past the last point?
    if (currentPoint != null && currentPoint.x < percent) {
      if (store == null) {
        return new ColorRGBA(currentPoint.color);
      } else {
        store.set(currentPoint.color);
        return store;
      }
    }


    if (store == null) {
      return new ColorRGBA(ColorRGBA.White);
    } else {
      store.set(ColorRGBA.White);
      return store;
    }
  }

  @Override
  public Gradient clone() {
    try {
      Gradient gradient = (Gradient) super.clone();
      gradient.points = new ArrayList<>();
      points.forEach((p)-> gradient.points.add(p.clone()));
      return gradient;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }


  public boolean equals(Object o) {
    if (!(o instanceof Gradient)) return false;

    Gradient check = (Gradient)o;

    if (points.size() != check.points.size()) return false;

    for (int i=0; i < points.size(); i++) {
      if (!points.get(i).equals(check.points.get(i)))
        return false;
    }

    return true;
  }
}
