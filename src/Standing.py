import string,time

files = ["tournament.txt"]

# Touch everything first
for file in files:
    open(file,"a").close()

while 1:
    for file in files:
        games= {}
        points = {}
        data = open(file,"r").readlines()
        for line in data:
            score,name = string.split(string.strip(line),":",1)
            if games.has_key(name):
                games[name] += 1
                points[name] += int(score)
            else:
                games[name] = 1
                points[name] = int(score)

    totals = []
    for key in games.keys():
        value = -float(points[key])/games[key]
        totals.append( (value,key,points[key],games[key]) )

    totals.sort()

    output = open("Standings.html","w")
    output.write('<HEAD><meta http-equiv="refresh" content="1" ></HEAD>')
    output.write("<BODY><TABLE BORDER=1 WIDTH=100%>\n")
    output.write("<TR><TH> Rank </TH><TH> Name </TH><TH> Score </TH><TH> Games </TH></TR>\n")
    for i in range(len(totals)):
        value,name,points,games = totals[i]
        output.write("<TR><TD ALIGN=CENTER>%d</TD><TD ALIGN=CENTER>%s</TD><TD ALIGN=CENTER>%f</TD><TD ALIGN=CENTER>%d</TD></TR>\n"%(i+1,name,-value,games))
    output.write("</TABLE></BODY>")
    output.close()
    print ".",        
    time.sleep(1)
    
