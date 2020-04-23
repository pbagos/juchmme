/*
 *   Copyright (C) 2020. Greenweaves Software Pty Ltd
 *   This is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with GNU Emacs.  If not, see <http://www.gnu.org/licenses/>
 *   REAR 	Reversal Distance
 */

package hmm;

public class testMeasure
{

    public static void main(String[] args)
    {
        String file;
        file=args[0];
        Params.FASTA = false;
        SeqSet testSet=new SeqSet( file);

        String[] p1 = testSet.getXs();
        String[] p2 = testSet.getOrigPath();

        Stats stats=new Stats( 	p1, p2, "M", "I", "O" );

        stats.Print();

        return;
    }

}
