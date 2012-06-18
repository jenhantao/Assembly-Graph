#!/usr/bin/env ruby
#=========================================================================
# optasm.rb [options] goalpart1 goalpart2 ...
#=========================================================================
#
#  -h --help          Display this message
#  -v --[no-]verbose  Verbose mode 
#
# Author : Christopher Batten
# Date   : Novemeber 18, 2008
# 

require 'optparse'
require 'rdoc/usage'
require 'stringio'
require 'pp'

# Enable ruby warnings (this avoid problems with "ruby -w")
$VERBOSE = true

#-------------------------------------------------------------------------
# Command line processing
#-------------------------------------------------------------------------

def usage()
  $stdout = StringIO::new
  RDoc::usage_no_exit
  STDOUT.puts($stdout.string.gsub(/\A=+\n(.*)\n\n=+/,"\n\\1\n"))
  exit(1)
end
def read_file()
  $opts[:file] = $ARGV
end
def set_sharing()
  $sharing = true
end
def set_dof()
  $dof = true
end
def set_exhaustive()
  $exhaustive = true
end
$dof = false
$sharing = false
$exhaustive = false
$opts = {}
def parse_cmdline()
  OptionParser.new do |opts|
    opts.on("-v", "--[no-]verbose") { |v| $opts[:verbose] = v }
    opts.on("-h", "--help")         { usage() }
    opts.on("-f", "--file") {read_file()}
    opts.on("-s", "--withsharing"){set_sharing()}
    opts.on("-d", "--withdof"){set_dof()}
    opts.on("-e", "--exhaustive"){set_exhaustive()}
  end.parse!

  $opts[:goalparts] = $ARGV
rescue
  usage()
end
#-------------------------------------------------------------------------
# Cost Class
#-------------------------------------------------------------------------

class Cost

  attr_accessor :stages, :steps, :sharing

  def initialize( stages, steps, sharing = 0 )
    @stages  = stages
    @steps   = steps
    @sharing = sharing
  end

 def <( rhs )
    if ( @stages < rhs.stages )
      return true
    elsif ( @stages == rhs.stages )
      if ( (@steps-@sharing) < (rhs.steps-rhs.sharing) )
        return true
      end
    end
    return false
  end

  def less_than( rhs, max_stages = 0 )
    if ((@stages <= max_stages) && (rhs.stages <= max_stages))
      return ((@steps-@sharing) < (rhs.steps-rhs.sharing))
    else
      if ( @stages < rhs.stages )
        return true
      elsif ( @stages == rhs.stages )
        if ( (@steps-@sharing) < (rhs.steps-rhs.sharing) )
          return true
        end
      end
      return false
    end
  end
 def equal_to( rhs, max_stages = 0 )
    if ((@stages <= max_stages) && (rhs.stages <= max_stages))
      return ((@steps-@sharing) == (rhs.steps-rhs.sharing))
    else
      if ( @stages < rhs.stages )
        return false
      elsif ( @stages == rhs.stages )
        if ( (@steps-@sharing) == (rhs.steps-rhs.sharing) )
          return true
        end
      end
      return false
    end
  end

  def less_than_ns( rhs, max_stages = 0 )
    if ((@stages <= max_stages) && (rhs.stages <= max_stages))
      return ((@steps) < (rhs.steps))
    else
      if ( @stages < rhs.stages )
        return true
      elsif ( @stages == rhs.stages )
        if ( (@steps) < (rhs.steps) )
          return true
        end
      end
      return false
    end
  end

 def equal_to_ns( rhs, max_stages = 0 )
    if ((@stages <= max_stages) && (rhs.stages <= max_stages))
      return ((@steps) == (rhs.steps))
    else
      if ( @stages < rhs.stages )
        return false
      elsif ( @stages == rhs.stages )
        if ( (@steps) == (rhs.steps) )
          return true
        end
      end
      return false
    end
  end

  def to_s()
    return "#{@stages}\t#{@steps}\t#{@sharing}"
  end

end

#-------------------------------------------------------------------------
# Assembly Tree Class
#-------------------------------------------------------------------------

class AssemblyTree

  attr_accessor :tree_str, :cost, :part, :subtrees, :dof

  def initialize()
    @tree_str    = ""
    @cost        = Cost::new(0,0)
    @subtrees    = []
    @part        = ""
    @dof      =0
  end

  def self.new_compound( left_tree, right_tree, sharing = 0 )
    tree = new()

    tree.part = "#{left_tree.part}#{right_tree.part}"
    tree.subtrees.push(left_tree)
    tree.subtrees.push(right_tree)
    tree.subtrees.concat(left_tree.subtrees)
    tree.subtrees.concat(right_tree.subtrees)

    tree.tree_str = "(#{left_tree}+#{right_tree})"

    if ( left_tree.cost.stages > right_tree.cost.stages )
      tree.cost.stages = left_tree.cost.stages
    else
      tree.cost.stages = right_tree.cost.stages
    end
    tree.cost.stages += 1

    tree.cost.steps   = left_tree.cost.steps   + right_tree.cost.steps + 1
    tree.cost.sharing = left_tree.cost.sharing + right_tree.cost.sharing + sharing
    tree.dof = left_tree.dof*right_tree.dof
    return tree
  end

  def self.new_compound_set( left_tree_set, right_tree_set, sharing = 0 )
    trees = Array.new;
    left_tree_set.each do |left_tree|
      right_tree_set.each do |right_tree|
        tree = new()

        tree.part = "#{left_tree.part}#{right_tree.part}"
        tree.subtrees.push(left_tree)
        tree.subtrees.push(right_tree)
        tree.subtrees.concat(left_tree.subtrees)
        tree.subtrees.concat(right_tree.subtrees)

        tree.tree_str = "(#{left_tree}+#{right_tree})"

        if ( left_tree.cost.stages > right_tree.cost.stages )
          tree.cost.stages = left_tree.cost.stages
        else
          tree.cost.stages = right_tree.cost.stages
        end
        tree.cost.stages += 1

        tree.cost.steps   = left_tree.cost.steps   + right_tree.cost.steps + 1
        tree.cost.sharing = left_tree.cost.sharing + right_tree.cost.sharing + sharing
        tree.dof = left_tree.dof*right_tree.dof
        trees.push(tree)
      end
    end
    return trees
  end


  def self.new_primitive( part )
    tree = new()
    tree.tree_str = "#{part}"
    tree.cost     = Cost::new(0,0)
    tree.subtrees = []
    tree.part     = [ part ]
    tree.dof = 1
    return tree
  end

  def clear_sharing()
    @subtrees.each do|sTree|
      sTree.cost.sharing = 0
    end
  end
  def to_s()
    return @tree_str
  end

end

#-------------------------------------------------------------------------
# Subpart Sharing
#-------------------------------------------------------------------------

class SubpartSharing < Hash

  def add_goalpart( goalpart )
    for len in 1 .. goalpart.length
      for pos in 0 .. (goalpart.length-len)
        subpart = goalpart[pos,len]
        if ( !has_key?(subpart) )
          self[subpart] = -1
        end
        self[subpart] += 1
      end
    end
  end

  def []( key )
    if ( has_key?(key) )
      super
    else
      return 0
    end
  end

end

#-------------------------------------------------------------------------
# Given an hash of AssmTrees, calc cost
#-------------------------------------------------------------------------
class SetCost
  def initialize(trees)
    @final_trees = trees
  end
  
  def execute()
  max_stages = 0
  all_rxns = {} 
  @final_trees.each do | goalpart, tree |
    if (tree.cost.stages > max_stages)
      max_stages = tree.cost.stages
    end
    #put subtrees into hash
    tree.subtrees.each do | subtree |
      if(!all_rxns.has_key?(subtree.to_s))
        all_rxns[subtree.to_s] = 0
      end
      all_rxns[subtree.to_s] +=1
    end
    if(!all_rxns.has_key?(tree.to_s))
        all_rxns[tree.to_s] = 0
      end
      all_rxns[tree.to_s] +=1
    end
    all_rxns.each_key { |key|
      if(key.length <3)
        all_rxns.delete(key)
      end
    }  
  num_rxns = all_rxns.length-1
  #all_rxns.each { |key, value| puts " #{key} - #{value} times" }
  #print "max stages:\t", max_stages, "\tnum rxns:\t", num_rxns.to_s, "\n"
  return Cost::new(max_stages,num_rxns)
  end
end

#-------------------------------------------------------------------------
# Exhaustive Algorithm
#-------------------------------------------------------------------------
class AlgoExhaustive
  def initialize(parts, final_trees)
    @parts = parts
    @asmTrees = Array.new
    @final_trees = final_trees
  end
  #parts is array
  def execute()
    @parts.each do | gp |
      @asmTrees.push(self.asmHelper(gp))
    end
    #calculate costs
    maxInt = 1
    bases = Array.new
    chooser = Array.new
    k =0
    @asmTrees.each do | treeA |
      maxInt *= treeA.length
      bases[k]=treeA.length
      chooser[k]=0
      k +=1
    end
    histogram ={}
    for z in 0..maxInt-1
      chooser[0] +=1
      for x in 0 .. @asmTrees.length-1
        if(chooser[x]%bases[x]==0 && chooser[x] != 0)
          chooser[x] = 0
          if(x+1 < @asmTrees.length)
            chooser[x+1] += 1
          end
        end
      end
      treeSet = {}
      for y in 0 ..@asmTrees.length-1
        treeSet[@parts[y]] =@asmTrees[y][chooser[y]]
      end
 
        treeSet = treeSet.merge(@final_trees)
        #puts "calculating exhaustive for parts: #{@parts} and final: #{@final_trees} --> #{treeSet}"
      #print treeSet.to_s, "\t"
      thisCost = SetCost::new(treeSet)
        
      mycost = Cost::new(0,0)
      mycost = thisCost.execute()
      
      if(!histogram.has_key?(mycost.to_s))
        histogram[mycost.to_s] = 0
      end
      histogram[mycost.to_s] +=1
    end
    puts "histogram:"
    histogram.each do |k,v|
      puts "#{k.to_s}\t#{v.to_s}"
    end
  end
  
  def asmHelper(part)
    trees = []
    if ( part.length == 1 )
      temp = AssemblyTree::new_primitive(part)
      trees[0] = temp
      return trees
    end
    k=0
    for i in 0..(part.length-2)
      left_part  = part[ 0   .. i ]
      right_part = part[ i+1 .. part.length-1 ]
      left_trees  = asmHelper( left_part )
      right_trees = asmHelper( right_part )
      left_trees.each do | lT |
        right_trees.each do | rT |
          trees[k] = AssemblyTree::new_compound( lT, rT )
          k += 1
        end
      end
    end
    return trees
  end
  
end


#-------------------------------------------------------------------------
# Standard Dynamic Programming Algorithm
#-------------------------------------------------------------------------

class AlgoStandardDP

  def initialize()
    @memoized_results = {}
  end

  def execute( part )

    # See if we have already computed this part
    if ( @memoized_results.has_key?(part) )
      return @memoized_results[part]
    end
   
    # See if this is a primitive part
    if ( part.length == 1 )
      return AssemblyTree::new_primitive(part)
    end
   
    # Create assembly trees for all possible partitions
    trees = []
    for i in 0..(part.length-2)
   
      left_part  = part[ 0   .. i ]
      right_part = part[ i+1 .. part.length-1 ]
   
      left_tree  = execute( left_part )
      right_tree = execute( right_part )
   
      trees[i] = AssemblyTree::new_compound( left_tree, right_tree )
    end
   
    # Find minimum cost assembly tree
    min_tree = trees[0]
    for i in 1 .. trees.length-1
      if ( trees[i].cost < min_tree.cost )
        min_tree = trees[i]
      end
    end
   
    # Add minimum cost assembly tree to hash and return it
    @memoized_results[part] = min_tree
    return min_tree
   
  end
   
end

#-------------------------------------------------------------------------
# Fake Shared Dynamic Programming Algorithm
#   only difference is we propagate a set of results instead of just one result
#-------------------------------------------------------------------------
class AlgoSDPSpace
  def initialize( sharing, lib )
    @lib = lib.dup
    @memoized_results = {}
    @sharing = sharing
  end
#part may be an array, always to_s
  def execute( part, max_stages, printStuff, makeFig)

    part_stages = "#{part.to_s}-#{max_stages}"

    # See if we have already computed this part
    if ( @memoized_results.has_key?(part_stages) )
      return @memoized_results[part_stages]
    end

    # Is part in library?
    if ( @lib.has_key?(part.to_s) )
      tree = @lib[part.to_s]
      tree.dof = 1
      @memoized_results[part_stages] = tree
      if(printStuff)
        puts "  #{part.to_s.ljust(10)} #{tree.cost.to_s.ljust(10)} #{tree.to_s}"
      end
      return tree
    end

    # See if this is a primitive part
    if ( part.length == 1 )
      return AssemblyTree::new_primitive(part)
    end
   
    # Create assembly tree sets for all possible partitions
    tree_sets = []
    for i in 0..(part.length-2)
   
      left_part  = part[ 0   .. i ]
      right_part = part[ i+1 .. part.length-1 ]
   
      left_tree_set  = execute( left_part, max_stages-1, printStuff, makeFig )
      right_tree_set = execute( right_part, max_stages-1, printStuff, makeFig )
      tree_sets[i] = AssemblyTree::new_compound_set( left_tree_set, right_tree_set, @sharing[part] )

    end
    min_tree_set = Array.new
    tree_sets.each do |trees|
    
      # Find minimum cost assembly tree
      min_tree = trees[0]
      min_tree_set.push(min_tree)
      if(printStuff)
        #puts "  #{part.to_s.ljust(10)} #{trees[0].cost.to_s.ljust(10)} #{trees[0].to_s} dof: #{trees[0].dof.to_s}"
      end
      for i in 1 .. trees.length-1
        if(printStuff)
          #puts "             #{trees[i].cost.to_s.ljust(10)} #{trees[i].to_s} dof: #{trees[i].dof.to_s}"
        end


        if($sharing)
          if(trees[i].cost.less_than( min_tree.cost, max_stages ))
            min_tree_set.clear
            min_tree_set = min_tree_set.push(trees[i])
          elsif(trees[i].cost.equal_to( min_tree.cost, max_stages ))
            min_tree_set = min_tree_set.push(trees[i])
          end     
        else
            if(trees[i].cost.less_than_ns( min_tree.cost, max_stages ))
            min_tree_set.clear
            min_tree_set = min_tree_set.push(trees[i])
          elsif(trees[i].cost.equal_to_ns( min_tree.cost, max_stages ))
            min_tree_set = min_tree_set.push(trees[i])
          end
        end        
      end
      #randomly choose a tree
     if(makeFig)
        #puts "Makefig: #{min_tree_set[rand(min_tree_set.length)]}"
        min_tree = min_tree_set[rand(min_tree_set.length)]
      end
      myDof = 0 #min_tree_set.length
      min_tree_set.each do |minT|
        myDof = myDof+minT.dof
      end
      # Add minimum cost assembly tree to hash and return it
      @memoized_results[part_stages] = min_tree
      min_tree.dof = myDof
    end
    return min_tree_set
   
  end
   
  
end

#-------------------------------------------------------------------------
# Shared Dynamic Programming Algorithm
#-------------------------------------------------------------------------

class AlgoSharedDP

  attr_accessor :memoized_results

  def initialize( sharing, lib )
    @lib = lib.dup
    @memoized_results = {}
    @sharing = sharing
  end
#part may be an array, always to_s
  def execute( part, max_stages, printStuff, makeFig)

    part_stages = "#{part.to_s}-#{max_stages}"

    # See if we have already computed this part
    if ( @memoized_results.has_key?(part_stages) )
      return @memoized_results[part_stages]
    end

    # Is part in library?
    if ( @lib.has_key?(part.to_s) )
      tree = @lib[part.to_s]
      tree.dof = 1
      @memoized_results[part_stages] = tree
      if(printStuff)
        puts "  #{part.to_s.ljust(10)} #{tree.cost.to_s.ljust(10)} #{tree.to_s}"
      end
      return tree
    end

    # See if this is a primitive part
    if ( part.length == 1 )
      return AssemblyTree::new_primitive(part)
    end
   
    # Create assembly trees for all possible partitions
    trees = []
    for i in 0..(part.length-2)
   
      left_part  = part[ 0   .. i ]
      right_part = part[ i+1 .. part.length-1 ]
   
      left_tree  = execute( left_part, max_stages-1, printStuff, makeFig )
      right_tree = execute( right_part, max_stages-1, printStuff, makeFig )
      trees[i] = AssemblyTree::new_compound( left_tree, right_tree, @sharing[part] )

    end
   
    # Find minimum cost assembly tree
    min_tree = trees[0]
    min_tree_set = Array.new
    min_tree_set.push(min_tree)
    if(printStuff)
      #puts "  #{part.to_s.ljust(10)} #{trees[0].cost.to_s.ljust(10)} #{trees[0].to_s} dof: #{trees[0].dof.to_s}"
    end
    for i in 1 .. trees.length-1
      if(printStuff)
        #puts "             #{trees[i].cost.to_s.ljust(10)} #{trees[i].to_s} dof: #{trees[i].dof.to_s}"
      end

        if(trees[i].cost.less_than_ns( min_tree.cost, max_stages ))
          min_tree_set.clear
          min_tree_set = min_tree_set.push(trees[i])
        elsif(trees[i].cost.equal_to_ns( min_tree.cost, max_stages ))
          min_tree_set = min_tree_set.push(trees[i])
        end
      if($sharing)
              if ( trees[i].cost.less_than( min_tree.cost, max_stages ) )
                min_tree = trees[i]
              end
      else
              if ( trees[i].cost.less_than_ns( min_tree.cost, max_stages ) )
                min_tree = trees[i]
              end            
      end
    end
    #randomly choose a tree
   if(makeFig)
      #puts "Makefig: #{min_tree_set[rand(min_tree_set.length)]}"
      min_tree = min_tree_set[rand(min_tree_set.length)]
    end
    myDof = 0 #min_tree_set.length
    min_tree_set.each do |minT|
      myDof = myDof+minT.dof
    end
    # Add minimum cost assembly tree to hash and return it
    @memoized_results[part_stages] = min_tree
    min_tree.dof = myDof
    return min_tree
   
  end
   
end

def rndUpbase2(num)
  log2 = Math.log(num)/Math.log(2)
  return log2.ceil
end
#-------------------------------------------------------------------------
# Main
#-------------------------------------------------------------------------

def main()
parse_cmdline()
printStuff =true
makeFig = false

goalparts = []
argGP = Array.new
  if($opts[:file])
    puts "#{$opts[:file]} called with sharing: #{$sharing.to_s} and dof: #{$dof.to_s}"
    File.open($opts[:file].to_s).each { |line|
      arr = line.split(" ")
        argGP = assign_new_names(arr)
    }
  end 


  # Find initial assembly tree for each goal part independently
  
  algo_standard = AlgoStandardDP::new
  initial_trees = {}
  goal_parts = Array.new
  argGP.each do | goalpart | 
    initial_trees[goalpart] = algo_standard.execute( goalpart )
    goal_parts.push(goalpart)
  end

  if(printStuff)
    puts "\n initial"
  end
  initial_trees.each do | goalpart, tree |
    if(printStuff)
      puts "  #{goalpart.to_s.ljust(10)} #{tree.cost.to_s.ljust(10)} #{tree.to_s}"
    end
  end

  # Determine max number of stages

  max_stages = 0
  initial_trees.each do | goalpart, tree |
    if ( tree.cost.stages > max_stages )
      max_stages = tree.cost.stages
    end
  end
  if(printStuff)
    puts "\n max stages = #{max_stages}"  
  end
  # Find subpart sharing

  sharing = SubpartSharing::new
  argGP.each do | goalpart | 
    sharing.add_goalpart(goalpart)
  end

  # Iteratively eliminate goal parts

  lib = {}
  final_trees = {}
  goalparts = argGP.dup

  while ( !goalparts.empty? ) do
  if($exhaustive)
    algo_exhaustive = AlgoExhaustive::new(goalparts, final_trees)
    algo_exhaustive.execute()
  end
    if(printStuff)
      puts "\n algo for "
      goalparts.each do |gpp|
       puts "#{gpp.join('.')};" 
       end
     end
    #for making the figure
      
     
    # Find trees with sharing and max stage constraint

    algo_shared = AlgoSharedDP::new(sharing,lib)
    shared_trees = {}
    goalparts.each do | goalpart |
      shared_trees[goalpart] = algo_shared.execute( goalpart, max_stages,  printStuff, makeFig)
    end
        #puts "LIBRARY"
        lib.each do |gp, tree|
          #puts "gp: #{gp} tree: #{tree}"
        end
    if(makeFig)
      #for figure making, concat final_trees (chosen trees) with shared_trees (gp yet to be pinned
      temp1 = final_trees
      temp1 = temp1.merge(shared_trees)
      totCost = SetCost::new(temp1)
      totCost.execute()
    end
    
    # Choose one of the goal parts with most stages & min dof
   
    min_goalpartdof = nil
    max_goalpart = nil
    max_tree     = nil
    shared_trees.each do | goalpart, tree |
      if ( max_tree == nil )
        min_goalpartdof = tree.dof
        max_tree     = tree
        max_goalpart = goalpart
      else
        max_cost = max_tree.cost
        cost     = tree.cost
        max_gp_len = rndUpbase2(max_goalpart.length)
        gp_len = rndUpbase2(goalpart.length)
        if($dof)
              if (    (gp_len > max_gp_len) \
                || (    (gp_len == max_gp_len) \
                     && ((tree.dof) < (min_goalpartdof))))
                min_goalpartdof = tree.dof
                max_tree     = tree
                max_goalpart = goalpart
              end
        else
             if (    (cost.stages > max_cost.stages) \
                  || (    (cost.stages == max_cost.stages) \
                       && ((cost.steps) > (max_cost.steps))))
                max_goalpart = goalpart
                max_tree     = tree
              end            
        end    
      end
    end
    
    if(printStuff)
      puts " best trees"
      shared_trees.each do | goalpart, tree |
        puts "  #{goalpart.to_s.ljust(10)} #{tree.cost.to_s.ljust(10)} #{tree.to_s} dof: #{tree.dof.to_s}"
      end
   end
    # Set sharing value for subparts of chosen goal part to zero

    lib[max_tree.part] = max_tree
    lib[max_tree.part].cost.steps = 0
    max_tree.subtrees.each do | subtree |
      lib[subtree.part] = subtree
      lib[subtree.part].cost.steps = 0
    end

    # Remove goal part from list of goal parts
    if(printStuff)
      puts " removed goal part = #{max_goalpart}" 
    end
    final_trees[max_goalpart] = max_tree
    goalparts.delete(max_goalpart)

  end

  if(printStuff)
    puts "\n final trees" #also compute total stages, steps
 end 
 max_stages = 0
  all_rxns = {}
  print "chosen"
  final_trees.each do | goalpart, tree |
      print "#{goalpart.to_s}#{tree.to_s}"
    end


  if(printStuff)
    final_trees.each do | goalpart, tree |
      puts "  #{goalpart.to_s.ljust(10)} #{tree.cost.to_s.ljust(10)} #{tree.to_s}"
    end
  end
  totCost = SetCost::new(final_trees)
  mycost = Cost::new(0,0)
  mycost = totCost.execute()
  puts "Assembly cost "<<mycost.to_s << " \n"
end



def assign_new_names(array_of_strs)
  #build hash of unique names
  myNames = {}
  tmpArray = Array.new
  array_of_strs = array_of_strs.sort
  array_of_strs.each do |str|
    myParts = str.split(".")
    tmpArray.push(myParts)
    myParts.each do |partname|
      if(!myNames.has_key?(partname.to_s))
        myNames[partname.to_s] = 0
      end
      myNames[partname.to_s] +=1
    end
  end
  if(myNames.length > 24)
    #key value pairs (key:name, value:translated name)
    # ascii a-z 97-122
    iter1 = 0
    iter2 = 0
    myNames.each do |key, value|
      char1 = iter1+97
      char2 = iter2+97
      myNames[key]=(char1).chr << (char2).chr
      iter2= iter2+1
      if(iter2%26==0 && iter2 > 0)
        iter1 = iter1+1
        iter2 = 0
      end
    end
    myNames.each do |k, v|
      puts "replacing #{k} with #{v}"  
    end
  
  #go through input array and replace names, also remove "."
  new_array= Array.new
  array_of_strs.each do |str|
    oldname = str
    oldarr = str.split(".")
    myNames.each do |key, value|
      for i in 0 ... oldarr.length
        if (oldarr[i] == key)
          oldarr[i] = value
        end
      end
    end
    str = oldarr.join(",")
    puts "replacing #{oldname} with #{str}"
    a = str_chunk(str, 2)
    new_array.push(oldarr)
    
  end
  return new_array
else
  return tmpArray
  end
end

#str, size of chunks
def str_chunk(str, chunk_size)
    new_array = Array.new
    k = 1
    s = ""
    str.each_byte do |char|
      s = s+char.chr
      if(k%chunk_size==0)
        new_array.push(s)
        s=""
      end
      k = k +1
    end
    return new_array
end
main()


