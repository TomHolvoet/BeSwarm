import pandas as pd
from ggplot import *
from PyPDF2 import PdfFileMerger
import os

data = pd.read_table('trajectories.log', header=None,
                     names=['index', 'time', 'posX', 'posY', 'posZ', 'yaw'], delim_whitespace=True)
data['index'] = data['index'].astype(str)

pos_x = ggplot(data, aes(x='time', y='posX', color='index')) + geom_path()
pos_y = ggplot(data, aes(x='time', y='posY', color='index')) + geom_path()
pos_z = ggplot(data, aes(x='time', y='posZ', color='index')) + geom_path()
yaw = ggplot(data, aes(x='time', y='yaw', color='index')) + geom_path()
pos_xy = ggplot(data, aes(x='posX', y='posY', color='index')) + geom_path()

pos_x.save('pos_x.pdf')
pos_y.save('pos_y.pdf')
pos_z.save('pos_z.pdf')
yaw.save('yaw.pdf')
pos_xy.save('pos_xy.pdf')

pdfs = ['pos_x.pdf', 'pos_y.pdf', 'pos_z.pdf', 'yaw.pdf', 'pos_xy.pdf']

outfile = PdfFileMerger()

for f in pdfs:
    outfile.append(open(f, 'rb'))

f = open('result.pdf', 'wb')
outfile.write(f)
f.close()

for file in pdfs:
    os.remove(file)
