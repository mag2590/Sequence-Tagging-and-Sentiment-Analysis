import fileinput
import re

def memm_prob(line,train_dict,fw,prev_sent,state):
    if len(fw)!= 0:
        return fw[(str(state)+'#'+str(prev_sent))]
    else:
        return 1
    
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
    viterbi = dict()
    backpointer = dict()
    new_review = False
    line_no = 0
    
    for line in fileinput.input(test_file):
        if line.find("[") == -1 and len(line) > 2:
            if line.find('{') != -1:
                prev_sent = 's'
            line = line.replace('{}','')
            line = line.replace('<','')
            line = line.replace('>','')
            
            if new_review:
                print(backpointer)
                new_review = False
                viterbi = dict()
                backpointer = dict()
                line_no = 0
                viterbi[(line_no,-2)] = 1
                viterbi[(line_no,-1)] = 1
                viterbi[(line_no,0)] = 1
                viterbi[(line_no,1)] = 1
                viterbi[(line_no,2)] = 1
            else:
                viterbi[(line_no,-2)] = viterbi[(line_no-1,-2)] * memm_prob(line,train_dict,fw,prev_sent,-2)
                viterbi[(line_no,-1)] = viterbi[(line_no-1,-1)] * memm_prob(line,train_dict,fw,prev_sent,-1)
                viterbi[(line_no,0)] = viterbi[(line_no-1,0)] * memm_prob(line,train_dict,fw,prev_sent,0)
                viterbi[(line_no,1)] = viterbi[(line_no-1,1)] * memm_prob(line,train_dict,fw,prev_sent,1)
                viterbi[(line_no,2)] = viterbi[(line_no-1,2)] * memm_prob(line,train_dict,fw,prev_sent,2)
                backpointer[line_no] = max(viterbi[(line_no,-2)],viterbi[(line_no,-1)],viterbi[(line_no,0)],viterbi[(line_no,1)],viterbi[(line_no,2)])
                if backpointer[line_no] == viterbi[(line_no,-2)]:
                    backpointer[line_no] = -2
                elif backpointer[line_no] == viterbi[(line_no,-1)]:
                    backpointer[line_no] = -1
                elif backpointer[line_no] == viterbi[(line_no,0)]:
                    backpointer[line_no] = 0
                elif backpointer[line_no] == viterbi[(line_no,1)]:
                    backpointer[line_no] = 1
                elif backpointer[line_no] == viterbi[(line_no,1)]:
                    backpointer[line_no] = 2    
    
            line_no += 1
            '''max_sent = 0
            max_prob = 0
            for k in fw.keys():
                if prev_sent == k.split('#')[-1] and max_prob < fw[k]:
                    max_prob = fw[k]
                    max_sent = k.split('#')[0]
            
            print(max_prob)'''
            
            '''max_enc = 0
            max_sent = 0
            for k in train_dict.keys():
                intr_len = len(set(k.split()).intersection(set(line.split())))        
                if intr_len > max_enc and prev_sent == train_dict[k][1]:
                    max_enc = intr_len
                    max_sent = train_dict[k][0]
            
            print(max_sent,max_enc)'''
        else:
            new_review = True
    
                        
main()