import fileinput
import re

def main():
    #Training portion    
    train_dict = dict()
    fw = dict()
    
    count = 0    
    train_file = input("Enter training file name: ")
    prev_sent = 's'
    
    for line in fileinput.input(train_file):
        if line.find("[") == -1 and len(line) > 2:
            if line.find('{') != -1:
                prev_sent = 's'
            line = line.replace('{}','')
            line = line.replace('<','')
            line = line.replace('>','')            
            
            sentiment_val = line.split()[-1]
            line = re.sub(r'-*\d+', '', line)
            
            train_dict[line] = [sentiment_val,prev_sent]
            count += 1
            
            if sentiment_val+'#'+prev_sent in fw.keys():
                fw[sentiment_val+'#'+prev_sent] += 1
            else:
                fw[sentiment_val+'#'+prev_sent] = 1
                
            prev_sent = sentiment_val
            
            
    for t in fw.keys():
        fw[t] /= float(count)
    #print(fw)
    #print(train_dict)
    
    #Testing portion
    test_file = input("Enter testing file name: ")
    
    for line in fileinput.input(test_file):
        if line.find("[") == -1 and len(line) > 2:
            if line.find('{') != -1:
                prev_sent = 's'
            line = line.replace('{}','')
            line = line.replace('<','')
            line = line.replace('>','')
            
            max_enc = 0
            max_sent = 0
            for k in train_dict.keys():
                intr_len = len(set(k.split()).intersection(set(line.split())))        
                if intr_len > max_enc and prev_sent == train_dict[k][1]:
                    max_enc = intr_len
                    max_sent = train_dict[k][0]
            
            print(max_sent,max_enc)
                    
main()